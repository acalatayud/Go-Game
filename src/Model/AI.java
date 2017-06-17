package Model;

import Service.Parameters;

import java.util.*;

/**
 * Created by Lucas on 10/06/2017.
 */
public class AI {

    private int[][][] influenceMaps;
    private int[][][] historyMap;
    private int lastMap = 0;
    private int[] xOff = {-1,1,0,0};
    private int[] yOff = {0,0,-1,1};
    private int influenceWeight = 1;
    private int potentialTerritoryWeight = 10;
    private int territoryWeight = 200;
    private int captureWeight = 200;
    private int player;
    private boolean scoutLayer;
    private DotBuilder dot;

    public AI(int player) {
        this.player = player;
        influenceMaps = new int[2][Parameters.boardSize][Parameters.boardSize];
        if(Parameters.dotTree)
            dot = new DotBuilder(player);
    }

    public Move getMove(Board board) {
        historyMap = new int[2][Parameters.boardSize][Parameters.boardSize];
        int otherPlayer = player == 1 ? 2 : 1;
        Move current = new Move(board, otherPlayer);
        Move bestMove;
        if(Parameters.prune) {
            if (Parameters.depth > 1)
                scoutLayer = true;
            bestMove = negamax(current, Parameters.depth, Parameters.worstValue, Parameters.bestValue, player);
        }
        else
            bestMove = negamaxNoPrune(current, Parameters.depth, player);
        return bestMove;
    }

    private Move negamax(Move move, int depth, int alpha, int beta, int player) {
        Board board = move.board;
        if(depth == 0 || board.gameFinished()) {
            move.value = ponderHeuristicValue(board, player);
            return move;
        }

        int otherPlayer = player == 1 ? 2 : 1;
        LinkedList<Move> children = generateMoves(board, player);
        if(scoutLayer) {
            scoutLayer = false;
            for(Move child : children) {
                child.board = board.duplicate();
                child.board.addPiece(child.x, child.y, player);
                child.weight = ponderHeuristicValue(child.board, player);
            }
            Collections.sort(children);
        }
        Board passBoard = board.duplicate();
        passBoard.pass(player);
        children.addFirst(new Move(passBoard, player));

        Move bestMove = new Move(Parameters.worstValue);
        for(Move child : children) {
            if(beta > alpha) {
                if (child.board == null) {
                    child.board = board.duplicate();
                    child.board.addPiece(child.x, child.y, player);
                }
                child.value = -negamax(child, depth - 1, -beta, -alpha, otherPlayer).value;

                if (child.value > bestMove.value)
                    bestMove = child;
                if (child.value > alpha)
                    alpha = child.value;
            }
            else
                child.pruned = true;

        }
        if(bestMove.board != passBoard )
            historyMap[player-1][bestMove.y][bestMove.x] += depth*depth;

        return bestMove;
    }

    private Move negamaxNoPrune(Move move, int depth, int player) {
        Board board = move.board;
        if(depth == 0 || board.gameFinished()) {
            move.value = ponderHeuristicValue(board, player);
            return move;
        }

        int otherPlayer = player == 1 ? 2 : 1;
        LinkedList<Move> children = generateMoves(board, player);
        Board passBoard = board.duplicate();
        passBoard.pass(player);
        children.addFirst(new Move(passBoard, player));

        Move bestMove = new Move(Parameters.worstValue);
        for(Move child : children) {
            if(child.board == null) {
                child.board = board.duplicate();
                child.board.addPiece(child.x, child.y, player);
            }
            child.value = -negamaxNoPrune(child, depth-1, otherPlayer).value;

            if(child.value > bestMove.value)
                bestMove = child;
        }

        return bestMove;
    }

    private LinkedList<Move> generateMoves(Board board, int player) {
        LinkedList<Move> moves = new LinkedList<>();
        for(int y = 0; y < Parameters.boardSize ; y++){
            for(int x = 0; x < Parameters.boardSize ; x++){
                if (board.lightVerifyMove(x,y,player)){
                    moves.add(new Move(x, y, historyMap[player-1][y][x], player));
                }
            }
        }
        Collections.sort(moves);
        return moves;
    }

    public int ponderHeuristicValue(Board board, int player){
        if(board.gameFinished()) {
            if(board.calculateWinner() == player)
                return Parameters.bestValue - 1;
            else
                return Parameters.worstValue + 1;
        }

        Stone[][] stones = board.getBoard();
        for (int y = 0; y < Parameters.boardSize; y++) {
            for (int x = 0; x < Parameters.boardSize; x++) {
                if(stones[y][x] != null) {
                    if(stones[y][x].getPlayer() == player)
                        influenceMaps[0][y][x] = 500;
                    else
                        influenceMaps[0][y][x] = -500;
                }
                else {
                    influenceMaps[0][y][x] = 0;
                }
            }
        }
        lastMap = 0;

        int value = 0;

        dilation(5);

        //System.out.println(calculateInfluence());

        value += influenceWeight * calculateInfluence();

        //printInfluenceMap();

        erosion(21);

        //System.out.println(calculateInfluence());
        value += potentialTerritoryWeight * calculateInfluence();

        //printInfluenceMap();

        int otherPlayer;
        if(player == 1)
            otherPlayer = 2;
        else
            otherPlayer = 1;

        //System.out.println(board.getPlayerCaptures(player));
        //System.out.println(board.getPlayerCaptures(otherPlayer));
        int[] territories = board.calculateTerritory();

        value += territoryWeight * (territories[player-1] - territories[otherPlayer-1]);

        value += captureWeight * (board.getPlayerCaptures(player) - board.getPlayerCaptures(otherPlayer));

        //System.out.println("Value: "+value);

        return value;
    }

    private int calculateInfluence() {
        int value;
        int influencePoints = 0;
        for (int y = 0; y < Parameters.boardSize; y++) {
            for (int x = 0; x < Parameters.boardSize; x++) {
                value = influenceMaps[lastMap][y][x];
                if(value != 0) {
                    if(value > 0) {
                        if (value < 200)
                            influencePoints++;
                    }
                    else {
                        if (value > -200)
                            influencePoints--;
                    }
                }
            }
        }
        return influencePoints;
    }

    private void dilation(int times) {
        int thisMap;

        for(int j = 0; j < times; j++) {

            if (lastMap == 0)
                thisMap = 1;
            else
                thisMap = 0;

            int positiveNeighbors;
            int negativeNeighbors;
            int value;

            for (int y = 0; y < Parameters.boardSize; y++) {
                for (int x = 0; x < Parameters.boardSize; x++) {
                    positiveNeighbors = 0;
                    negativeNeighbors = 0;

                    for (int i = 0; i < 4; i++) {
                        switch (i) {
                            case 0:
                                if (x == 0)
                                    continue;
                                break;
                            case 1:
                                if (x == Parameters.boardSize - 1)
                                    continue;
                                break;
                            case 2:
                                if (y == 0)
                                    continue;
                                break;
                            case 3:
                                if (y == Parameters.boardSize - 1)
                                    continue;
                                break;
                        }

                        int neighbor = influenceMaps[lastMap][y + yOff[i]][x + xOff[i]];
                        if (neighbor != 0) {
                            if (neighbor > 0)
                                positiveNeighbors++;
                            else
                                negativeNeighbors++;
                        }
                    }

                    value = influenceMaps[lastMap][y][x];
                    influenceMaps[thisMap][y][x] = value;

                    if (value >= 0 && negativeNeighbors == 0)
                        influenceMaps[thisMap][y][x] += positiveNeighbors;

                    else if (value <= 0 && positiveNeighbors == 0)
                        influenceMaps[thisMap][y][x] -= negativeNeighbors;

//                    else
//                        influenceMaps[thisMap][y][x] = 0;
                }
            }

            lastMap = thisMap;
        }
    }

    private void erosion(int times) {
        int thisMap;

        for(int j = 0; j < times; j++) {

            if (lastMap == 0)
                thisMap = 1;
            else
                thisMap = 0;

            int positiveNeighbors;
            int negativeNeighbors;
            int neutralNeighbors;
            int value;
            int diff;

            for (int y = 0; y < Parameters.boardSize; y++) {
                for (int x = 0; x < Parameters.boardSize; x++) {

                    value = influenceMaps[lastMap][y][x];
                    influenceMaps[thisMap][y][x] = value;

                    if(value != 0) {
                        positiveNeighbors = 0;
                        negativeNeighbors = 0;
                        neutralNeighbors = 0;

                        for (int i = 0; i < 4; i++) {
                            switch (i) {
                                case 0:
                                    if (x == 0)
                                        continue;
                                    break;
                                case 1:
                                    if (x == Parameters.boardSize - 1)
                                        continue;
                                    break;
                                case 2:
                                    if (y == 0)
                                        continue;
                                    break;
                                case 3:
                                    if (y == Parameters.boardSize - 1)
                                        continue;
                                    break;
                            }

                            int neighbor = influenceMaps[lastMap][y + yOff[i]][x + xOff[i]];
                            if (neighbor != 0) {
                                if (neighbor > 0)
                                    positiveNeighbors++;
                                else
                                    negativeNeighbors++;
                            }
                            else
                                neutralNeighbors++;
                        }

                        if (value > 0) {
                            diff = neutralNeighbors + negativeNeighbors;
                            if(value <= diff)
                                influenceMaps[thisMap][y][x] = 0;
                            else
                                influenceMaps[thisMap][y][x] -= diff;
                        }
                        else {
                            diff = neutralNeighbors + positiveNeighbors;
                            if(value >= -diff)
                                influenceMaps[thisMap][y][x] = 0;
                            else
                                influenceMaps[thisMap][y][x] += diff;
                        }
                    }
                }
            }

            lastMap = thisMap;
        }
    }

    private void printInfluenceMap() {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_WHITE = "\u001B[37m";
        String color;

        for (int y = 0; y < Parameters.boardSize; y++) {
            for (int x = 0; x < Parameters.boardSize; x++) {
                if(influenceMaps[lastMap][y][x] > 0)
                    color = ANSI_GREEN;
                else if(influenceMaps[lastMap][y][x] < 0)
                    color = ANSI_RED;
                else
                    color = ANSI_WHITE;
                System.out.print(color+String.format("%1$4s", influenceMaps[lastMap][y][x])+ANSI_RESET);
            }
            System.out.println("");
        }
        System.out.println("");
    }
}

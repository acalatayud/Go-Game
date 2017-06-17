package Model;

import Service.Constants;

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
    private int territoryWeight = 100;
    private int captureWeight = 100;
    private int player;
    private boolean scoutLayer;
    private DotBuilder dot;

    private class Move implements Comparable{
        public int x;
        public int y;
        public int value;
        public int weight;
        public Board board;

        public Move(int x, int y, int weight) {
            this.x = x;
            this.y = y;
            this.weight = weight;
        }

        public Move(Board board) {
            this.board = board;
        }

        public Move(int value) {
            this.value = value;
        }

        /** Esta invertido a proposito para que el orden quede descendente*/
        @Override
        public int compareTo(Object o) {
            if(o instanceof Move)
                return ((Move) o).weight - weight;
            else
                return 0;
        }
    }

    public AI(int player) {
        this.player = player;
        influenceMaps = new int[2][Constants.boardSize][Constants.boardSize];
        if(Constants.dotTree)
            dot = new DotBuilder(player);
    }

    public Board getMove(Board board) {
        historyMap = new int[3][Constants.boardSize][Constants.boardSize]; //Reducir de 3 a 2
        Move current = new Move(board);
        if(Constants.depth > 1)
            scoutLayer = true;
        Move bestMove = negamax(current, Constants.depth, Constants.worstValue, Constants.bestValue, player);
        return bestMove.board;
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
        children.addFirst(new Move(passBoard));

        Move bestMove = new Move(Constants.worstValue);
        for(Move child : children) {
            if(child.board == null) {
                child.board = board.duplicate();
                child.board.addPiece(child.x, child.y, player);
            }
            child.value = -negamax(child, depth-1, -beta, -alpha, otherPlayer).value;

            if(child.value > bestMove.value)
                bestMove = child;
            if(child.value > alpha)
                alpha = child.value;
            if(alpha >= beta)
                break;
        }
        if(bestMove.board != passBoard )
            historyMap[player][bestMove.y][bestMove.x] += depth*depth;

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
        children.addFirst(new Move(passBoard));

        Move bestMove = new Move(Constants.worstValue);
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
        /*ArrayList<Node> children = generateMoves(board,player);
        if (depth==0 || board.gameFinished()) { // If depth reached or is terminal node (only possibility is pass)
            current.setHeuristicValue(Model.ponderHeuristicValue(board, this.player));
            return current;
        }

        int upNext = player == 1 ? 2 : 1;
        Board boardNew;

        for (Node child : children ) {
            boardNew = board.duplicate();
            boardNew.addPiece(child.getxPos(), child.getyPos(), child.getPlayer());
            depthNoPrune(boardNew, child, upNext, depth - 1);
            if(Constants.dotTree) {
                dot.addEdge(current, child);
                dot.setLabel(child);
            }
        }

        Node best = player == this.player ? Collections.max(children) : Collections.min(children);
        current.setHeuristicValue(best.getHeuristicValue());
        if(Constants.dotTree) {
            dot.changeColor(best, "red");
            dot.setLabel(current);
        }
        return best;*/
    }

    private LinkedList<Move> generateMoves(Board board, int player) {
        LinkedList<Move> moves = new LinkedList<>();
        for(int y=0; y < Constants.boardSize ; y++){
            for(int x=0; x < Constants.boardSize ; x++){
                if (board.lightVerifyMove(x,y,player)){
                    moves.add(new Move(x, y, historyMap[player][y][x]));
                }
            }
        }
        Collections.sort(moves);
        return moves;
    }

    public int ponderHeuristicValue(Board board, int player){
        if(board.gameFinished()) {
            if(board.calculateWinner() == player)
                return Constants.bestValue - 1;
            else
                return Constants.worstValue + 1;
        }

        Stone[][] stones = board.getBoard();
        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
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
        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
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

            for (int y = 0; y < Constants.boardSize; y++) {
                for (int x = 0; x < Constants.boardSize; x++) {
                    positiveNeighbors = 0;
                    negativeNeighbors = 0;

                    for (int i = 0; i < 4; i++) {
                        switch (i) {
                            case 0:
                                if (x == 0)
                                    continue;
                                break;
                            case 1:
                                if (x == Constants.boardSize - 1)
                                    continue;
                                break;
                            case 2:
                                if (y == 0)
                                    continue;
                                break;
                            case 3:
                                if (y == Constants.boardSize - 1)
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

            for (int y = 0; y < Constants.boardSize; y++) {
                for (int x = 0; x < Constants.boardSize; x++) {

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
                                    if (x == Constants.boardSize - 1)
                                        continue;
                                    break;
                                case 2:
                                    if (y == 0)
                                        continue;
                                    break;
                                case 3:
                                    if (y == Constants.boardSize - 1)
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

        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
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

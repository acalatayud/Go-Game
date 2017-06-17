package Model;

import Service.Parameters;
import Controller.Controller;

import java.util.Random;

/**
 * Created by juan on 23/05/17.
 */
public class Model {

    private Board board;
    //int[][] koBoard;

    private static int[][][] influenceMaps = new int[2][Parameters.boardSize][Parameters.boardSize];
    private static int lastMap = 0;
    private static int[] xOff = {-1,1,0,0};
    private static int[] yOff = {0,0,-1,1};
    private static int influenceWeight = 1;
    private static int potentialTerritoryWeight = 10;
    private static int territoryWeight = 100;
    private static int captureWeight = 100;
    private int AIplayer;


    public Model(Board board, int AIplayer){
        if(board ==null)
            throw new NullPointerException("Board can't be null");
        if(AIplayer != 1 && AIplayer != 2)
            throw new IllegalArgumentException("AIplayer must be 1 or 2");

        this.board = board;
        this.AIplayer = AIplayer;
    }

    public Board getBoard() {
        return board;
    }

    public boolean addPiece(int x, int y, int player) {
        if(player != AIplayer && player == board.getPlayerN() && board.addPiece(x, y, player)) {
            board.nextPlayer();
            return true;
        }
        return false;
    }

    public boolean pass(int player) {
        if(player != AIplayer && player == board.getPlayerN()) {
            board.pass(player);
            board.nextPlayer();
            return true;
        }
        return false;
    }

    @Deprecated
    public static int ponderHeuristicValue(Board board, int player){//por el momento dejo static
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

    public static void test1() {
        lastMap = 0;
        int[][] in =
                        {{0,0,0,0,0,0,0,0,0,0,0,0,256},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,256,0,256,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0}};

        int[][] out = {{0,0,0,0,0,0,0,0,0,0,1,2,258},
                        {0,0,0,0,0,0,0,0,0,0,0,2,2},
                        {0,0,0,0,0,0,0,0,0,0,0,0,1},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,1  ,0,1  ,0,0,0,0},
                        {0,0,0,0,0,2,2  ,3,2  ,2,0,0,0},
                        {0,0,0,0,1,2,260,4,260,2,1,0,0},
                        {0,0,0,0,0,2,2  ,3,2  ,2,0,0,0},
                        {0,0,0,0,0,0,1  ,0,1  ,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0}};

        for (int y = 0; y < Parameters.boardSize; y++) {
            for (int x = 0; x < Parameters.boardSize; x++) {
                influenceMaps[0][y][x] = in[y][x];
            }
        }

        dilation(2);

        for (int y = 0; y < Parameters.boardSize; y++) {
            for (int x = 0; x < Parameters.boardSize; x++) {
                if(influenceMaps[lastMap][y][x] != out[y][x]) {
                    System.out.println("DILATION FAILED | value is "+influenceMaps[lastMap][y][x]+" and should be "+out[y][x]);
                }
            }
        }
    }

    public static void test2() {
        lastMap = 0;
        int[][] in =
                {{0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,-256,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,-256,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,256,0,256,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,256,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0}};

        for (int y = 0; y < Parameters.boardSize; y++) {
            for (int x = 0; x < Parameters.boardSize; x++) {
                influenceMaps[0][y][x] = in[y][x];
            }
        }

        dilation(5);

    //    printInfluenceMap();
     //   System.out.println("");
       // System.out.println("");

        erosion(21);

        //printInfluenceMap();


    }

    public static void test3() {
        Board board = new Board();
        Random rand = new Random(1);
        for(int i=0; i < 50; i++) {
            board.addPiece(rand.nextInt(13), rand.nextInt(13), rand.nextInt(2) + 1);
        }
        //System.out.println(board);

        long start = System.nanoTime();

        for(int i=0; i < 100000; i++)
            ponderHeuristicValue(board, 1);

        System.out.println((System.nanoTime() - start)/1000000);
    }

    private static int calculateInfluence() {
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

    private static int calculatePotentialTerritory() {
        return 0;
    }

    private static void printInfluenceMap() {
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

    private static void dilation(int times) {
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

    private static void erosion(int times) {
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

    public Board getAIMove(Board board){
        System.out.println("getaimove");
        GameTree tree = new GameTree(board,2);
        Node move = tree.buildTree(board);
        System.out.println("move is:" + move);
        if(move.getxPos()==-1&&move.getyPos()==-1)
            return null;

        board.addPiece(move.getxPos(),move.getyPos(),2);
        return board;
    }



    public void gameLoop(){

        AI ai = new AI(AIplayer);

        while(!board.gameFinished()){
            try {
                Thread.sleep(10);
            }
            catch (Exception e){
                continue;
            }

            if(board.getPlayerN() == AIplayer) {
                System.out.println("entro al if");
                long start = System.nanoTime();
                board = ai.getMove(board);
                System.out.println((System.nanoTime() - start)/1000000);
                board.nextPlayer();
                Controller.updateView(board);
            }

        }
        int winner = board.calculateWinner();
        // Mandar por pantalla el ganador
        System.out.println("winner is: "+winner);
    }

    public void executeFileMode(int player){
        GameTree tree = new GameTree(board,player);
        Node move = tree.buildTree(board);
        System.out.println(move);
    }

//    public void storeKO(Board board){
//        for(int i=0;i<Parameters.boardSize;i++){
//            for(int j=0;j<Parameters.boardSize;j++){
//                koBoard[i][j] = board.checkSpace(j,i);
//            }
//        }
//    }
//
//    public boolean violatesKO(Board board){
//        for(int i=0;i<Parameters.boardSize;i++){
//            for(int j=0;j<Parameters.boardSize;j++){
//                if (board.checkSpace(j,i)!=koBoard[j][i])
//                    return false;
//            }
//        }
//        return true;
//    }

}

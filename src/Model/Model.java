package Model;

import Service.Constants;

import java.util.ArrayList;
import java.util.Random;

import static Controller.Controller.waitForPlayerMove;

/**
 * Created by juan on 23/05/17.
 */
public class Model {

    Board board;

    private static int[][][] influenceMaps = new int[2][Constants.boardSize][Constants.boardSize];
    private static int lastMap = 0;
    private static int[] xOff = {-1,1,0,0};
    private static int[] yOff = {0,0,-1,1};
    private static int influenceWeight = 1;
    private static int potentialTerritoryWeight = 10;
    private static int territoryWeight = 10;
    private static int captureWeight = 10;


    public Model(Board board){
        this.board = board;
    }

    /**
     * El tema es el siguiente, la catedra pide que hagamos un algoritmo
     * determinista y resulta que no existe una funcion que pondera el valor
     * de una movida en GO de manera determinista, entonces propongo lo siguiente:
     *
     * El valor heurístico es un int que comienza en 0 y no tiene cotas, ya que se darán naturalmente con el sistema
     * El método analizaria varios criterios y en base a eso sumaria y restaria puntaje a la jugada
     * El puntaje sería ponderado y requeriría análisis para determinar un buen número
     * Por ejemplo:
     *     el valor comienza en 0
     *     suma 30 por cada pieza enemiga que se adquiera en la jugada
     *     suma 20 por cada territorio que se capture
     *     suma 10 por cada paso en la direccion de formar una figura fuerte
     *     (una figura fuerte es una figura tácticamente ventajosa, facil de defender
     *     como por ejemplo el bamboo:
     *     0 1 0 1 0
     *     0 1 0 1 0
     *     ese sería un bamboo para el jugador 1)
     *     resta 30 por cada pieza dejada a morir
     *     resta 20 por cada territorio propio dejado sin defender
     *     resta 10 por cada paso en la direccion de formar una figura débil (piezas aisladas)
     *
     *     también leí que hay una zona media donde es más conveniente poner piezas (cerca de las enemigas pero no tan aisladas)
     *     habria que estudias más el tema
     *
     *     (habría que poner muchos más criterios y pensar un algoritmo que pueda revisar todos en la
     *     menor cantidad de recorridos, tambien hay que analizar la relación entre
     *     cuanto afecta el criterio y cuanto aumenta la complejidad, por ejemplo:
     *     si hay un criterio cuyo valor ponderado es +- 3 y aumenta la complejidad drásticamente conviene
     *     dejarlo afuera)
     *
     *     En cuanto a las cotas, existiría una cota natural fácilmente calculable
     *     ya que hay cantidad de posiciones finitas, dicha cota
     *     (sea +- 13*13*30 , osea +- 65910) entonces ese valor positivo sería el valor heurístico
     *     de una jugada que gana la partida, y el valor negativo sería el valor de una
     *     jugada que resulta en perder el partido, de esta manera se prioriza la victoria ante cualquier criterio
     **/
    public static int ponderHeuristicValue(Board board, int player){//por el momento dejo static
        Stone[][] stones = board.getBoard();
        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
                if(stones[y][x] != null) {
                    if(stones[y][x].getPlayer() == player)
                        influenceMaps[0][y][x] = 500;
                    else
                        influenceMaps[0][y][x] = -500;
                }
            }
        }
        lastMap = 0;

        int value = 0;

        dilation(5);

        System.out.println(calculateInfluence());

        value += influenceWeight * calculateInfluence();

        printInfluenceMap();

        erosion(21);

        System.out.println(calculateInfluence());
        value += potentialTerritoryWeight * calculateInfluence();

        printInfluenceMap();

        int otherPlayer;
        if(player == 1)
            otherPlayer = 2;
        else
            otherPlayer = 1;

        System.out.println(board.getPlayerCaptures(player));
        System.out.println(board.getPlayerCaptures(otherPlayer));

        value += captureWeight * (board.getPlayerCaptures(player) - board.getPlayerCaptures(otherPlayer));

        System.out.println("Value: "+value);

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

        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
                influenceMaps[0][y][x] = in[y][x];
            }
        }

        dilation(2);

        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
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

        for (int y = 0; y < Constants.boardSize; y++) {
            for (int x = 0; x < Constants.boardSize; x++) {
                influenceMaps[0][y][x] = in[y][x];
            }
        }

        dilation(5);

        printInfluenceMap();
        System.out.println("");
        System.out.println("");

        erosion(21);

        printInfluenceMap();


    }

    public static void test3() {
        Board board = new Board();
        Random rand = new Random();
        for(int i=0; i < 1000; i++) {
            board.addPiece(rand.nextInt(13), rand.nextInt(13), rand.nextInt(2) + 1);
        }
        System.out.println(board);
        ponderHeuristicValue(board, 1);
    }

    private static int calculateInfluence() {
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

    private static int calculatePotentialTerritory() {
        return 0;
    }

    private static int calculateTerritory() {
        return 0;
    }

    private static void printInfluenceMap() {
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

    public Board getAIMove(Board board){
        GameTree tree = new GameTree(board,2);
        Node move = tree.buildTree(board);
        if(move.getxPos()==-1&&move.getyPos()==-1)
            return null;

        board.addPiece(move.getxPos(),move.getyPos(),2);
        return board;
    }



    public void gameLoop(){

        while(!board.gameFinished()){
            int playerTurn = board.getPlayerN();
            //System.out.println(playerTurn);
            if(playerTurn == 2) {
                Board auxBoard = getAIMove(board);
                if (auxBoard == null)
                    board.pass(playerTurn);
                else
                    board = auxBoard;
                //esto probablemente se pueda optimisar
                Controller.Controller.updateView(board);
                playerTurn = 1;
            }

        }
        int winner = board.calculateWinner();
        // Mandar por pantalla el ganador
    }

    public void executeFileMode(Board board, int player){
        GameTree tree = new GameTree(board,player);
        Node move = tree.buildTree(board);
        System.out.println(move);
    }
}

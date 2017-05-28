package Model;

import Service.Constants;

import java.util.ArrayList;

import static Controller.Controller.waitForPlayerMove;

/**
 * Created by juan on 23/05/17.
 */
public class Model {
    public int ponderHeuristicValue(Board board){
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
         */
        return 0;
    }

    public Board getAIMove(Board board){
        return new Board();//dummy para q compile
    }

    /**
     * Calculates territory held by both players. It does not return individual territory
     * because the scores are always sought in pairs, so the most efficient solution is to calculate them both.
     * @param realBoard
     * @return integer array, element 0 is player 1's territory, element 1 is player 2's territory.
     *
     * PSEUDOCODE:
     *     for each column
     *         for each row
     *             if space is not visited
     *                 flood fill (and keep borders)
     *             if only one player has borders
     *                 add points
     *
     */
    public static int[] calculateTerritory(Board realBoard){
        Board mockBoard = new Board(); // 0 is unvisited, 1 is visited

        // First array: 0 is out of board, 1 is player 1, 2 is player 2
        // Second array: Set cardinal = spaces counted
        ArrayList<ArrayList<Integer>> borders = new ArrayList<>();
        borders.add(new ArrayList<Integer>());
        borders.add(new ArrayList<Integer>());

        int[] points = {0,0}; // return parameter: First element represents player 1's points, second element represents player 2's points.
        boolean player1=false;
        boolean player2=false;

        for (int i=0; i< Constants.boardSize; i++){
            for (int j=0; j<Constants.boardSize; j++){
                if (mockBoard.checkSpace(i,j)==0)
                    floodFill(mockBoard,realBoard,i,j,borders);

                for(Integer space : borders.get(0)){
                    if(player1&&player2)
                        break;
                    if(space==1)
                        player1=true;
                    if(space==2)
                        player2=true;
                }

                if(player1&&!player2){
                    points[0]+=borders.get(1).size();
                }
                if(player2&&!player1){
                    points[1]+=borders.get(1).size();
                }

                player1=false;
                player2=false;
                borders = new ArrayList<>();
                borders.add(new ArrayList<Integer>());
                borders.add(new ArrayList<Integer>());
            }
        }
        return points;
    }

    private static ArrayList<ArrayList<Integer>> floodFill(Board mockBoard, Board realBoard, int xPos, int yPos, ArrayList<ArrayList<Integer>> borders){
        int space;
        if( xPos<0 || xPos>12 || yPos<0 || yPos>12){
            borders.get(0).add(0);
            return borders;
        }
        if (mockBoard.checkSpace(xPos,yPos)==1) {// If space is already visited.
            borders.get(0).add(realBoard.checkSpace(xPos,yPos));
            return borders;
        }
        else {
            mockBoard.addPiece(xPos, yPos, 1); // Mark as visited.
        }

        if ((space=realBoard.checkSpace(xPos,yPos))!=0){
            borders.get(0).add(space);
            return borders;
        }
        else
            borders.get(1).add(1);

        floodFill(mockBoard,realBoard,xPos,yPos-1,borders);// North
        floodFill(mockBoard,realBoard,xPos+1,yPos,borders);// East
        floodFill(mockBoard,realBoard,xPos,yPos+1,borders);// South
        floodFill(mockBoard,realBoard,xPos-1,yPos,borders);// West

        return borders;
    }

    /**
     * Calculates which player has the most points.
     * @param board
     * @return integer (1 or 2) representing the player who won
     */
    public int calculateWinner(Board board){
        return 1;
    }

    public void gameLoop(int playerTurn, Board board){
        while(!board.gameFinished()){
            switch(playerTurn){
                case 1:
                    board = waitForPlayerMove(board);
                    //actualizar por pantalla tablero
                    playerTurn = 2;
                    break;
                case 2:
                    board = getAIMove(board);
                    //actualizar por pantalla tablero
                    playerTurn = 1;
                    break;
                default:
                    throw new IllegalArgumentException("gameLoop received an illegal playerTurn integer");
            }
        }
        int winner = calculateWinner(board);
        // Mandar por pantalla el ganador
    }
}

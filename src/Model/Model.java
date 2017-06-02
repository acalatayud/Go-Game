package Model;

import Service.Constants;

import java.util.ArrayList;

import static Controller.Controller.waitForPlayerMove;

/**
 * Created by juan on 23/05/17.
 */
public class Model {
    public static int ponderHeuristicValue(Board board, int player){//por el momento dejo static
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
        GameTree tree = new GameTree(board);
        Node move = tree.buildTree(board,2);
        if(move.getxPos()==-1&&move.getyPos()==-1)
            return null;

        board.addPiece(move.getxPos(),move.getyPos(),2);
        return board;
    }

    public void gameLoop(Board board, int playerTurn, boolean dotTreeMode){
        Board auxBoard = new Board();
        
        while(!board.gameFinished()){
            switch(playerTurn){
                case 1:
                    auxBoard = waitForPlayerMove(board);
                    if(auxBoard == null)
                        board.pass(playerTurn);
                    else
                        board = auxBoard;
                    //actualizar por pantalla tablero
                    playerTurn = 2;
                    break;
                case 2:
                    auxBoard = getAIMove(board);
                    if(auxBoard == null)
                        board.pass(playerTurn);
                    else
                        board = auxBoard;
                    //actualizar por pantalla tablero
                    playerTurn = 1;
                    break;
                default:
                    throw new IllegalArgumentException("gameLoop received an illegal playerTurn integer");
            }
        }
        int winner = board.calculateWinner();
        // Mandar por pantalla el ganador
    }

    public void executeFileMode(Board board, int player){

    }
}

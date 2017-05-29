package Model;

import Service.Constants;

import java.util.ArrayList;

/**
 *  Header structure for the Game Tree
 */
public class GameTree {
    private Board board;

    public GameTree(Board board){
        this.board = board;
    }

    /**
     * Construye el árbol y retorna el Node representando
     * la mejor jugada encontrada,
     * @return
     */
    private Node buildTree(){
        return new Node(0,0,0);//dummy
    }

    public ArrayList<Node> generateMoves(Board board, int player) {
        ArrayList<Node> moves = new ArrayList<>();
        Node toAdd;

        for(int i=0; i < Constants.boardSize ; i++){
            for(int j=0; j < Constants.boardSize ; j++){
                if (board.checkSpace(i,j)!=0)
                    break;
                toAdd = new Node(i,j,player);
                toAdd.setHeuristicValue(Model.ponderHeuristicValue(board));
                moves.add(toAdd);
            }
        }
        return moves;
    }
}

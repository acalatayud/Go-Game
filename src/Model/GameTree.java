package Model;

import Service.Constants;

import java.util.ArrayList;

/**
 *  Header structure for the Game Tree
 */
public class GameTree {
    private Node root;

    public GameTree(Board board){

    }

    private Board buildTree(Board board){
        return new Board();//dummy
    }

    public ArrayList<Node> makeMoves(Board board, boolean isPLayer) {
        ArrayList<Node> moves = new ArrayList<>();

        for(int i=0; i < Constants.boardSize ; i++){
            for(int j=0; j < Constants.boardSize ; j++){
                if (board.checkSpace(i,j)!=0)
                    break;
                moves.add(new Node(i,j,isPLayer));
            }
        }
        return moves;
    }
}

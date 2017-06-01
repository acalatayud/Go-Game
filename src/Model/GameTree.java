package Model;

import Service.Constants;

import java.util.ArrayList;
import java.util.Collections;

/**
 *  Header structure for the Game Tree
 */
public class GameTree {
    private Board board;
    private boolean isDepth;

    public GameTree(Board board, boolean isDepth){
        this.board = board;
        this.isDepth = isDepth;
    }

    /**
     * Construye el árbol y retorna el Node representando
     * la mejor jugada encontrada,
     * @return Node
     */
    private Node buildTree(Board board, int upNext){
        return depthNoPrune(board, new Node(-1,-1,upNext),upNext, 0);
    }

    private Node depthWithPune(Board board, Node node, int depth, int alfa, int beta, int player){
        return new Node(2,3,4);
    }

    private Node depthNoPrune(Board board, Node current, int player, int depth) {
        ArrayList<Node> moves = generateMoves(board,player);
        if (depth==0 || (moves.size()==1)) { // If depth reached or is terminal node (only possibility is pass)
            current.setHeuristicValue(Model.ponderHeuristicValue(board, player));
            return current;
        }

        int upNext = player == 1 ? 2 : 1;

        current.setChildren(generateMoves(board, player));
        ArrayList<Node> children = current.getChildren();

        Board boardNew;

        for (Node node : children ) {
            boardNew = board.duplicate();
            boardNew.addPiece(node.getxPos(), node.getyPos(), node.getPlayer());
            depthNoPrune(boardNew, node, upNext, depth - 1);
        }

        Node best = depth % 2 == 1 ? Collections.max(children) : Collections.min(children);
        current.setHeuristicValue(best.getHeuristicValue());
        return best;
    }

    public ArrayList<Node> generateMoves(Board board, int player) {
        ArrayList<Node> moves = new ArrayList<>();
        Node toAdd;

        for(int i=0; i < Constants.boardSize ; i++){
            for(int j=0; j < Constants.boardSize ; j++){
                if (board.checkSpace(i,j)!=0)
                    break;
                toAdd = new Node(i,j,player);
                moves.add(toAdd);
            }
        }
        toAdd = new Node(-1,-1,player);
        toAdd.setHeuristicValue(0);
        moves.add(toAdd); // Pass

        return moves;
    }
}

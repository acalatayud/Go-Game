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

    public boolean maxReached(int depth){
        return false;
    }

    /**
     * Construye el árbol y retorna el Node representando
     * la mejor jugada encontrada,
     * @return Node
     */
    private Node buildTree(Board board, int upNext){
        return buildTreeRecursive(board, new Node(-1,-1,upNext),upNext, 0);
    }

    private Node buildTreeRecursive(Board board, Node current, int player, int depth) {
        if (maxReached(depth)) {
            current.setHeuristicValue(Model.ponderHeuristicValue(board));
            return current;
        }

        int upNext = player == 1 ? 2 : 1;

        ArrayList<Node> children = new ArrayList<>();
        current.setChildren(generateMoves(board, player));

        Board boardNew;

        for (Node node : children) {
            boardNew = board.duplicate();
            boardNew.addPiece(node.getxPos(), node.getyPos(), node.getPlayer());
            buildTreeRecursive(boardNew, node, upNext, depth + 1);
        }

        Node best = depth % 2 == 1 ? Collections.max(children) : Collections.min(children);
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
                toAdd.setHeuristicValue(Model.ponderHeuristicValue(board));
                moves.add(toAdd);
            }
        }
        toAdd = new Node(-1,-1,player);
        toAdd.setHeuristicValue(0);
        moves.add(toAdd); // Pass

        return moves;
    }
}

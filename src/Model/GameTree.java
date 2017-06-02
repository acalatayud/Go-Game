package Model;

import Service.Constants;

import java.util.ArrayList;
import java.util.Collections;

import static Service.Constants.depth;

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
     * @return Node
     */
    public Node buildTree(Board board, int upNext){
        if (Constants.prune){
            if (Constants.depth != -1){
                return depthWithPune(board,new Node(-1,-1,2),Constants.depth,Constants.worstValue,Constants.bestValue,2);
            }
            else {
                return null; // return timeWithPrune()
            }

        }
        else {
            if (Constants.depth != -1){
                return depthNoPrune(board,new Node(-1,-1,2),2,Constants.depth);
            }
            else {
                return null; // return timeNoPrune()
            }
        }
    }

    private Node depthWithPune(Board board, Node node, int depth, int alpha, int beta, int player){
        ArrayList<Node> children = generateMoves(board,player);
        if(depth==0||(children.size()==1)){
            node.setHeuristicValue(Model.ponderHeuristicValue(board,player));
            return node;
        }
        Board boardNew = board.duplicate();
        boardNew.addPiece(node.getxPos(), node.getyPos(), player);

        if (player==2){ // Maximizing
            node.setHeuristicValue(Constants.worstValue);
            for (Node child : children){
                node.setHeuristicValue(Math.max(node.getHeuristicValue(),depthWithPune(boardNew,child,depth-1,alpha,beta,1).getHeuristicValue()));
                alpha = Math.max(alpha,node.getHeuristicValue());
                if (beta<=alpha)
                    break;
            }
        }
        else{ // Minimizing
            node.setHeuristicValue(Constants.bestValue);
            for(Node child : children){
                node.setHeuristicValue(Math.min(node.getHeuristicValue(),depthWithPune(boardNew,child,depth-1,alpha,beta,2).getHeuristicValue()));
                beta = Math.min(beta, node.getHeuristicValue());
                if (beta<=alpha)
                    break;
            }
        }
        return node;
    }

    private Node depthNoPrune(Board board, Node current, int player, int depth) {
        ArrayList<Node> children = generateMoves(board,player);
        if (depth==0 || (children.size()==1)) { // If depth reached or is terminal node (only possibility is pass)
            current.setHeuristicValue(Model.ponderHeuristicValue(board, player));
            return current;
        }

        int upNext = player == 1 ? 2 : 1;
        current.setChildren(children);
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

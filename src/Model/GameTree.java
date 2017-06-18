package Model;

import Service.Parameters;

import java.util.ArrayList;
import java.util.Collections;


/**
 *  Header structure for the Game Tree
 */
public class GameTree {
    private Board board;
    private int player;
    private DotBuilder b;

    public GameTree(Board board, int player){
        this.board = board;
        this.player = player;
        if(Parameters.dotTree)
        	this.b = new DotBuilder(player);
    }

    /**
     * Construye el �rbol y retorna el Node representando
     * la mejor jugada encontrada,
     * @return Node
     */
    public Node buildTree(Board board){
    	Node move = null;
        if (Parameters.prune){
            if (Parameters.depth != -1)
                move = depthWithPrune(board,new Node(-2,-2,0), Parameters.depth, Parameters.worstValue, Parameters.bestValue,player);
            else {
                return null; // return timeWithPrune()
            }
        }
        else {
            if (Parameters.depth != -1)
                move = depthNoPrune(board,new Node(-2,-2,0),player, Parameters.depth);
            else {
                return null; // return timeNoPrune()
            }
        }
        if(Parameters.dotTree)
            b.close();
        return move;
    }

    private Node depthWithPrune(Board board, Node node, int depth, int alpha, int beta, int player){
        ArrayList<Node> children = generateMoves(board,player);
        if(depth==0||board.gameFinished()){
            node.setHeuristicValue(Model.ponderHeuristicValue(board,this.player));
            return node;
        }

        int upNext = player == 1 ? 2 : 1;
        int startingValue = player == this.player ? Parameters.worstValue: Parameters.bestValue;
        node.setHeuristicValue(startingValue);
        Node selected = new Node(-1,-1, player);
        selected.setHeuristicValue(startingValue);

        for (Node child : children){
        	Board boardNew = board.duplicate();
            boardNew.addPiece(child.getxPos(), child.getyPos(), player);
        	if(beta>alpha){
        		depthWithPrune(boardNew,child,depth-1,alpha,beta,upNext);
        		if(player==this.player) {
        			node.setHeuristicValue(Math.max(node.getHeuristicValue(),child.getHeuristicValue()));
        			if(child.compareTo(selected) > 0)
        				selected = child;
        			alpha = Math.max(alpha,node.getHeuristicValue());
        		}
        		else {
        			node.setHeuristicValue(Math.min(node.getHeuristicValue(),child.getHeuristicValue()));
        			if(child.compareTo(selected) < 0)
        				selected = child;
        			beta = Math.min(beta, node.getHeuristicValue());
        		}
        	}
        	else
        		child.setColor(2); //Pruned
        	if(Parameters.dotTree) {
        		//b.addEdge(node, child);
                //b.setLabel(child);
        	}
        }
        
        if(Parameters.dotTree) {
            //b.changeColor(selected, "red");
            //b.setLabel(node);
        }
        return selected;
    }

    private Node depthNoPrune(Board board, Node current, int player, int depth) {
    	ArrayList<Node> children = generateMoves(board,player);
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
            if(Parameters.dotTree) {
                //b.addEdge(current, child);
                //b.setLabel(child);
            }
        }

        Node best = player == this.player ? Collections.max(children) : Collections.min(children);
        current.setHeuristicValue(best.getHeuristicValue());
        if(Parameters.dotTree) {
            //b.changeColor(best, "red");
            //b.setLabel(current);
        }
        return best;
    }



    public ArrayList<Node> generateMoves(Board board, int player) {
        ArrayList<Node> moves = new ArrayList<>();
        Node toAdd;

        for(int i = 0; i < Parameters.boardSize ; i++){
            for(int j = 0; j < Parameters.boardSize ; j++){
                if (board.verifyMove(j,i,player)){
                toAdd = new Node(j,i,player);
                moves.add(toAdd);
                }
            }
        }
        toAdd = new Node(-1,-1,player);
        toAdd.setHeuristicValue(0);
        moves.add(toAdd); // Pass

        return moves;
    }
}

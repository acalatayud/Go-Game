package Model;

import java.util.ArrayList;

/**
 *  The AI's quality is determined by how deep the Game Tree is able
 *  to go (how far ahead can he see) so instead of storing a Board in
 *  each Node we store only the movement information, represented
 *  by two ints and a boolean value.
 */
public class Node {
    private ArrayList<Node> children;
    private boolean isPlayer;
    private int xPos;
    private int yPos;
    private int heuristicValue;

    public Node(int xPos, int yPos, boolean isPlayer){
        this.xPos = xPos;
        this.yPos = yPos;
        this.isPlayer = isPlayer;
    }

    public void addChild(int xPos, int yPos, boolean isPlayer){
        children.add(new Node(xPos,yPos,isPlayer));
    }

    public void prune(Node node){
        children.remove(node);// Búsqueda por índice u objeto?
    }
}

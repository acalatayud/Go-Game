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
    private int player;
    private int xPos;
    private int yPos;
    private int heuristicValue;

    public Node(int xPos, int yPos, int player){
        this.xPos = xPos;
        this.yPos = yPos;
        this.player = player;
    }

    public void addChild(int xPos, int yPos, int player){
        children.add(new Node(xPos,yPos,player));
    }

    public void setHeuristicValue(int value){
        this.heuristicValue = value;
    }

    public void prune(Node node){
        children.remove(node);// Búsqueda por índice u objeto?
    }
}

package Model;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *  The AI's quality is determined by how deep the Game Tree is able
 *  to go (how far ahead can he see) so instead of storing a Board in
 *  each Node we store only the movement information, represented
 *  by two ints and a boolean value.
 */
public class Node implements Comparable<Node>{
    // xPos,yPos = -1 represents a pass
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

    public void setChildren(ArrayList<Node> children){
        this.children = children;
    }

    public void setHeuristicValue(int value){
        this.heuristicValue = value;
    }

    public void prune(Node node){
        children.remove(node);// Búsqueda por índice u objeto?
    }

    @Override
    public int compareTo(Node node) {
        if (node==null)
            return 1;
        return this.heuristicValue-node.heuristicValue;
    }

    public int getPlayer() {
        return player;
    }

    public int getyPos() {
        return yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getHeuristicValue() {

        return heuristicValue;
    }
}

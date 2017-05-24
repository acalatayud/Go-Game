package Model;

import java.util.ArrayList;

/**
 * Created by juan on 24/05/17.
 */
public class Node {
    private ArrayList<Node> children;
    private Board data;

    public Node(Board data){
        this.data = data;
        children = new ArrayList<>();
    }

    public void addChild(Board board){
        children.add(new Node(board));
    }

    public void prune(Node node){
        children.remove(node);// Búsqueda por índice u objeto?
    }
}

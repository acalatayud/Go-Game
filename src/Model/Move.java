package Model;

public class Move implements Comparable{
    public int x;
    public int y;
    public int value;
    public int weight;
    public Board board;
    public int id;
    public int player;
    public boolean pruned;

    public Move(int x, int y, int weight, int player) {
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.player = player;
    }

    public Move(Board board, int player) {
        this.board = board;
        this.player = player;
    }

    public Move(int value) {
        this.value = value;
    }

    /** Esta invertido a proposito para que el orden quede descendente*/
    @Override
    public int compareTo(Object o) {
        if(o instanceof Move)
            return ((Move) o).weight - weight;
        else
            return 0;
    }

    @Override
    public String toString() {
        if(x == -1 && y == -1)
            return "PASS";
        if(x == -2 && y == -2)
            return "START";
        return "(" + x +", " + y +")";
    }
}

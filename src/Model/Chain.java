package Model;

import java.util.ArrayList;

/**
 * Created by Lucas on 26/05/2017.
 */
public class Chain {
    private ArrayList<Stone> stones;
    private short liberties;
    private boolean immortal;

    public Chain() {
        stones = new ArrayList<>();
        liberties = 0;
    }

    public void addStone(Stone stone) {
        stones.add(stone);
        liberties += stone.getLiberties();
    }

    /**Returns amount of captured stones**/
    public int updateLiberties(int diff) {
        liberties += diff;
        if(liberties == 0) {
            int chainSize = stones.size();
            kill();
            return chainSize;
        }
        else
            return 0;
    }

    private void kill() {
        stones = null;
    }

    public short getLiberties() {
        return liberties;
    }

    public boolean isImmortal() {
        return immortal;
    }

    public ArrayList<Stone> getStones() {
        return stones;
    }

    public Chain join(Chain other) {
        if(this != other) {
            ArrayList<Stone> otherStones = other.getStones();
            for (Stone stone : otherStones) {
                stone.setChain(this);
            }
            stones.addAll(otherStones);
            liberties += other.getLiberties();
        }
        return this;
    }
}

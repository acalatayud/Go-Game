package Model;

import java.util.ArrayList;

/**
 * Created by Lucas on 26/05/2017.
 */
public class Stone {
    private byte x;
    private byte y;
    private byte player;
    private Chain chain;
    private byte liberties;

    public Stone(byte x, byte y, byte player, byte liberties, Chain chain) {
        this.x = x;
        this.y = y;
        this.player = player;
        this.liberties = liberties;
        this.chain = chain;
        chain.addStone(this);
    }

    public Stone clone(Chain chain){
        return new Stone(x,y,player,liberties,chain);
    }

    public Stone(byte x, byte y, byte player) {
        this(x, y, player, (byte)4, new Chain());
    }

    public Stone(byte x, byte y, byte player, byte liberties) {
        this(x, y, player, liberties, new Chain());
    }

    /**Returns amount of captured stones**/
    public ArrayList<Stone> decLiberties() {
        liberties--;
        return chain.updateLiberties(-1);
    }

    public void incLiberties() {
        liberties++;
        chain.updateLiberties(1);
    }

    public void setLiberties(byte liberties) {
        chain.updateLiberties(liberties - this.liberties);
        this.liberties = liberties;
    }

    public byte getLiberties() {
        return liberties;
    }

    public byte getPlayer() {
        return player;
    }

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public boolean isImmortal() {
        return chain.isImmortal();
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}

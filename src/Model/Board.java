package Model;

import Service.Constants;
import java.util.HashSet;

/**
 * The board is represented by a HashSet of player Pieces, a HashSet of AI Pieces,
 * and a primitive array with:
 *     0 representing an empty position
 *     1 representing a position held by the player
 *     2 representing a position held by the AI
 */
public class Board {
    private static class Piece{
        private int player;
        private Integer xPos;
        private Integer yPos;

        public Piece(int xPos, int yPos, int player){
            this.xPos = xPos;
            this.yPos = yPos;
            this.player = player;
        }

        public int hashCode() {
            int result = 17;
            result = 31 * result + xPos.hashCode();
            result = 31 * result + yPos.hashCode();
            return result;
        }
    }

    private HashSet<Piece> playerPieces;
    private HashSet<Piece> aiPieces;
    private int[][] board = new int[Constants.boardSize][Constants.boardSize];

    public Board(){
        playerPieces = new HashSet<>();
        aiPieces = new HashSet<>();
    }

    public boolean addPiece(int xPos, int yPos, int player){
        if (board[yPos][xPos]!=0)
            return false;
        else{
            board[yPos][xPos]=player;
            playerPieces.add(new Piece(xPos,yPos,player));
            return true;
        }
    }

    public int checkSpace(int xPos, int yPos){
        return board[yPos][xPos];
    }

    public int playerPiecesCardinal(){
        return playerPieces.size();
    }

    public int aiPiecesCardinal(){
        return aiPieces.size();
    }

    public boolean gameFinished(){
        if (playerPieces.size()+aiPieces.size()== Constants.boardSize*Constants.boardSize)
            return true;
        // Faltan casos
        return false;
    }

    //For debugging
    public String toString(){
        String ans = "";
        for(int i=0;i<13;i++){
            ans+= "[";
            for(int j=0;j<13;j++){
                ans+=this.checkSpace(i,j);
                ans+="   ";
            }
            ans+= "]\n";
        }
        return ans;
    }
}

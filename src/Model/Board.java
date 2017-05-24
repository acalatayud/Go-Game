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
        private boolean isPlayer;
        private Integer xPos;
        private Integer yPos;

        public Piece(int xPos, int yPos, boolean isPlayer){
            this.xPos = xPos;
            this.yPos = yPos;
            this.isPlayer = isPlayer;
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
    private char[][] board = new char[Constants.boardSize][Constants.boardSize];

    public Board(){
        playerPieces = new HashSet<>();
        aiPieces = new HashSet<>();
    }

    public boolean addPiece(int xPos, int yPos, boolean isPlayer){
        if (board[xPos][yPos]!=0)
            return false;
        else{
            if (isPlayer){
                board[xPos][yPos]=1;
                playerPieces.add(new Piece(xPos,yPos,true));
            }
            else {
                board[xPos][yPos]=2;
                aiPieces.add(new Piece(xPos,yPos,false));
            }
            return true;
        }
    }

    public int checkSpace(int xPos, int yPos){
        return board[xPos][yPos];
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
}

package Model;

import Service.Constants;

import java.util.ArrayList;
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
    int player1Captures;
    int player2Captures;
    private int[][] board = new int[Constants.boardSize][Constants.boardSize];

    public Board(){
        playerPieces = new HashSet<>();
        aiPieces = new HashSet<>();
        player1Captures=0;
        player2Captures=0;
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

    /**
     * Calculates territory held by both players. It does not return individual territory
     * because the scores are always sought in pairs, so the most efficient solution is to calculate them both.
     * @param realBoard
     * @return integer array, element 0 is player 1's territory, element 1 is player 2's territory.
     *
     * PSEUDOCODE:
     *     for each column
     *         for each row
     *             if space is not visited
     *                 flood fill (and keep borders)
     *             if only one player has borders
     *                 add points
     *
     */
    public int[] calculateTerritory(Board realBoard){
        Board mockBoard = new Board(); // 0 is unvisited, 1 is visited

        // First array: 0 is out of board, 1 is player 1, 2 is player 2
        // Second array: Set cardinal = spaces counted
        ArrayList<ArrayList<Integer>> borders = new ArrayList<>();
        borders.add(new ArrayList<Integer>());
        borders.add(new ArrayList<Integer>());

        int[] points = {0,0}; // return parameter: First element represents player 1's points, second element represents player 2's points.
        boolean player1=false;
        boolean player2=false;

        for (int i=0; i< Constants.boardSize; i++){
            for (int j=0; j<Constants.boardSize; j++){
                if (mockBoard.checkSpace(i,j)==0)
                    floodFill(mockBoard,realBoard,i,j,borders);

                for(Integer space : borders.get(0)){
                    if(player1&&player2)
                        break;
                    if(space==1)
                        player1=true;
                    if(space==2)
                        player2=true;
                }

                if(player1&&!player2){
                    points[0]+=borders.get(1).size();
                }
                if(player2&&!player1){
                    points[1]+=borders.get(1).size();
                }

                player1=false;
                player2=false;
                borders = new ArrayList<>();
                borders.add(new ArrayList<Integer>());
                borders.add(new ArrayList<Integer>());
            }
        }
        return points;
    }

    private ArrayList<ArrayList<Integer>> floodFill(Board mockBoard, Board realBoard, int xPos, int yPos, ArrayList<ArrayList<Integer>> borders){
        int space;
        if( xPos<0 || xPos>12 || yPos<0 || yPos>12){
            borders.get(0).add(0);
            return borders;
        }
        if (mockBoard.checkSpace(xPos,yPos)==1) {// If space is already visited.
            borders.get(0).add(realBoard.checkSpace(xPos,yPos));
            return borders;
        }
        else {
            mockBoard.addPiece(xPos, yPos, 1); // Mark as visited.
        }

        if ((space=realBoard.checkSpace(xPos,yPos))!=0){
            borders.get(0).add(space);
            return borders;
        }
        else
            borders.get(1).add(1);

        floodFill(mockBoard,realBoard,xPos,yPos-1,borders);// North
        floodFill(mockBoard,realBoard,xPos+1,yPos,borders);// East
        floodFill(mockBoard,realBoard,xPos,yPos+1,borders);// South
        floodFill(mockBoard,realBoard,xPos-1,yPos,borders);// West

        return borders;
    }

    /**
     * Calculates which player has the most points.
     * @return integer (1 or 2 or 0) representing the player who won, or 0 if it is a tie
     */
    public int calculateWinner(){
        int[] territory = calculateTerritory(this);
        int winner;
        if ( (winner = territory[0]+this.player1Captures - (territory[1]+this.player2Captures)) == 0)
            return winner;

        return winner > 0 ? 1 : 2;
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

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
    /*private static class Piece{
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
    }*/
    private int playerN = 1;
    private ArrayList<HashSet<Stone>> playerPieces;
    private int[] playerCaptures = new int[2];
    private Stone[][] board = new Stone[Constants.boardSize][Constants.boardSize];
    private boolean[] playerPassed = new boolean[2];

    public Stone[][] getBoard(){
        return board;
    }
    public int getPlayerN(){
        return playerN;}


    public Board(){
        playerPieces = new ArrayList<>(2);
        playerPieces.add(new HashSet<Stone>());
        playerPieces.add(new HashSet<Stone>());
        playerCaptures[0] = 0;
        playerCaptures[1] = 0;
        playerPassed[0] = false;
        playerPassed[1] = false;
    }
    //Este duplicate no sirve para el minimax, hay que clonar todas las piezas
    public Board duplicate(){
        Board newBoard = new Board();
        newBoard.board = board.clone();
        newBoard.playerCaptures = playerCaptures.clone();
        newBoard.playerPieces = (ArrayList<HashSet<Stone>>) playerPieces.clone();
        newBoard.playerPassed = playerPassed.clone();
        return newBoard;
    }
    public void nextPlayer(){
        if(playerN == 1)
            playerN = 2;
        else
            playerN = 1;
    }
    public boolean addPiece(int x, int y, int player){
        if (outOfBounds(x, y) || board[y][x]!= null || violatesSuicide(x, y, player) || violatesKo(x, y, player)) {
            System.out.println("piece could not be added to the model");
            return false;
        }
        else{
            int liberties = 4;
            HashSet<Chain> samePlayerChains = new HashSet<>(4);
            Stone neighbor = null;
            ArrayList<Stone> capturedStones;
            //TODO: Modularizar
            //En violateSuicide se deberian recorrer los 4 neighbors
            //Podriamos hacerlo mas eficiente si obtenemos los neghibors
            //una sola vez para evitar chequear dos veces las condiciones
            for(int i=0; i<4 ; i++){
                switch(i){
                    case 0:
                        if(!(x > 0 && (neighbor = board[y][x-1]) != null))
                            continue;
                        break;
                    case 1:
                        if(!(x < Constants.boardSize - 1 && (neighbor = board[y][x+1]) != null))
                            continue;
                        break;
                    case 2:
                        if(!(y > 0 && (neighbor = board[y-1][x]) != null))
                            continue;
                        break;
                    case 3:
                        if(!(y < Constants.boardSize - 1 && (neighbor = board[y+1][x]) != null))
                            continue;
                        break;
                    default:
                        break;
                }
                liberties--;
                if(neighbor.getPlayer() == player)
                    samePlayerChains.add(neighbor.getChain());
                else {
                    capturedStones = neighbor.decLiberties();
                    if(capturedStones != null) {
                        for(Stone stone : capturedStones)
                            board[stone.getY()][stone.getX()] = null;
                    }
                }
            }

            Chain newChain = new Chain();
            for(Chain chain : samePlayerChains) {
                newChain.join(chain);
            }

            Stone stone = new Stone((byte)x, (byte)y, (byte)player, (byte)liberties, newChain);
            board[y][x] = stone;
            System.out.println("piece was added to the model");
            return true;
        }
    }

    public boolean outOfBounds(int x, int y) {
        return x < 0 || x >= Constants.boardSize || y < 0 || y >= Constants.boardSize;
    }

    public boolean violatesSuicide(int x, int y, int player) {
        boolean player1=false;
        boolean player2=false;
        Stone neighbor=null;

        for(int i=0; i<4 ; i++){
            switch(i){
                case 0:
                    if(!(x > 0 && (neighbor = board[y][x-1]) != null))
                        continue;
                    break;
                case 1:
                    if(!(x < Constants.boardSize - 1 && (neighbor = board[y][x+1]) != null))
                        continue;
                    break;
                case 2:
                    if(!(y > 0 && (neighbor = board[y-1][x]) != null))
                        continue;
                    break;
                case 3:
                    if(!(y < Constants.boardSize - 1 && (neighbor = board[y+1][x]) != null))
                        continue;
                    break;
                default:
                    break;
            }
            if(neighbor.getPlayer()==1)
                player1=true;
            if(neighbor.getPlayer()==2)
                player2=true;
        }

        return player==1?(!player1&&player2):(player1&&!player2);
    }

    public boolean violatesKo(int x, int y, int player) {
        return false;
    }

    public int checkSpace(int xPos, int yPos){
        return board[yPos][xPos] == null ? 0 : board[yPos][xPos].getPlayer();
    }

    public int playerPiecesCardinal(int player){
        return playerPieces.get(player-1).size();
    }

    public void pass(int player){
        playerPassed[player-1] = true; //TODO: Resetear si en el turno siguiente no pasa
    }

    public boolean gameFinished(){
        if (playerPieces.get(0).size()+playerPieces.get(1).size() == Constants.boardSize*Constants.boardSize)
            return true;

        return playerPassed[0] && playerPassed[1];
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
        if ( (winner = territory[0]+playerCaptures[0] - (territory[1]+playerCaptures[1])) == 0)
            return winner;

        return winner > 0 ? 1 : 2;
    }

    //For debugging
    public String toString(){
        String ans = "";
        for(int i=0;i<13;i++){
            ans+= "[";
            for(int j=0;j<13;j++){
                ans+=this.checkSpace(j,i);
                ans+="   ";
            }
            ans+= "]\n";
        }
        return ans;
    }
}

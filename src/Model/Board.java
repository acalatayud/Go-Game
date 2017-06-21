package Model;

import Service.Parameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

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

    //zobristTable indices: empty=0, black=1, white=2
    //169x3 table
    ArrayList<Integer> zobristIndices = new ArrayList<>();
    private static long[][] zobristTable = null;
    private long zobristHash;
    private long prevZobristHash;
    private int playerN = 1;
    private HashSet<Chain> chains;
    private int[] playerCaptures = new int[2];
    private Stone[][] board = new Stone[Parameters.boardSize][Parameters.boardSize];
    private boolean[] playerPassed = new boolean[2];

    public Stone[][] getBoard(){
        return board;
    }
    public int getPlayerN(){
        return playerN;
    }


    public Board(){
        if(zobristTable == null)
            initializeZobrist();

        chains = new HashSet<Chain>();
        prevZobristHash = 0;
        zobristHash = zobristHash();
        playerCaptures[0] = 0;
        playerCaptures[1] = 0;
        playerPassed[0] = false;
        playerPassed[1] = false;
    }

    public Board duplicate() {
        Board newBoard = new Board();
        newBoard.zobristIndices = (ArrayList<Integer>) zobristIndices.clone();
        newBoard.prevZobristHash = prevZobristHash;
        newBoard.zobristHash = zobristHash;
        newBoard.playerCaptures = playerCaptures.clone();
        newBoard.playerPassed = playerPassed.clone();
        newBoard.playerN = this.playerN;

        for (Chain chain : chains) {
            Chain newChain = new Chain();
            for (Stone stone : chain.getStones()) {
                Stone newStone = new Stone(stone.getX(), stone.getY(), stone.getPlayer(), stone.getLiberties(), newChain);
                newBoard.board[stone.getY()][stone.getX()] = newStone;
            }
            newBoard.chains.add(newChain);
        }

        return newBoard;
    }
    public void nextPlayer(){
        if(playerN == 1)
            playerN = 2;
        else
            playerN = 1;
    }

    public void mockAddPiece(int x, int y, int player){
        board[y][x] = new Stone((byte)x,(byte)y,(byte)player);
    }

    public boolean addPiece(int x, int y, int player){
        long newHash = zobristHash;
        ArrayList<Integer> oldZobristIndices = (ArrayList<Integer>)zobristIndices.clone();
        if (!verifyMove(x, y, player)) {
            if(x == -1 && y == -1)
                pass(player);

            return false;
        }
        else{

            HashSet<Chain> oldChains = new HashSet<>();
            int[] oldPlayerCaptures = playerCaptures.clone();
            Stone[][] oldBoard = new Stone[Parameters.boardSize][Parameters.boardSize];

            for (Chain chain : chains) {
                Chain newChain = new Chain();
                for (Stone stone : chain.getStones()) {
                    Stone newStone = new Stone(stone.getX(), stone.getY(), stone.getPlayer(), stone.getLiberties(), newChain);
                    oldBoard[stone.getY()][stone.getX()] = newStone;
                }
                oldChains.add(newChain);
            }

            int liberties = 4;
            HashSet<Chain> samePlayerChains = new HashSet<>(4);
            ArrayList<Stone> samePlayerStones = new ArrayList<>(4);
            ArrayList<Stone> otherPlayerStones = new ArrayList<>(4);
            Stone neighbor = null;
            ArrayList<Stone> capturedStones;

            for(int i=0; i<4 ; i++){
                switch(i){
                    case 0:
                        if(x == 0) {
                            liberties--;
                            continue;
                        }
                        neighbor = board[y][x-1];
                        break;
                    case 1:
                        if(x == Parameters.boardSize - 1) {
                            liberties--;
                            continue;
                        }
                        neighbor = board[y][x+1];
                        break;
                    case 2:
                        if(y == 0) {
                            liberties--;
                            continue;
                        }
                        neighbor = board[y-1][x];
                        break;
                    case 3:
                        if(y == Parameters.boardSize - 1) {
                            liberties--;
                            continue;
                        }
                        neighbor = board[y+1][x];
                        break;
                }

                if(neighbor == null)
                    continue;

                liberties--;

                if(neighbor.getPlayer() == player) {
                    samePlayerStones.add(neighbor);
                    samePlayerChains.add(neighbor.getChain());
                }
                else {
                    otherPlayerStones.add(neighbor);
                    capturedStones = neighbor.decLiberties();
                    if(capturedStones != null) {

                        playerCaptures[player-1] += capturedStones.size();
                        chains.remove(capturedStones.get(0).getChain());
                        for(Stone stone : capturedStones) {
                            if(otherPlayerStones.contains(stone))
                                liberties++;
                            int stoneYIndex = stone.getY();
                            int stoneXIndex = stone.getX();
                            board[stoneYIndex][stoneXIndex] = null;
                            newHash = zobristXor(newHash,stoneXIndex,stoneYIndex,0);
                        }
                        updateSurroundings(capturedStones);
                    }
                }
            }
            //capturedstones remuevo,
            Chain newChain = new Chain();
            for(Chain chain : samePlayerChains) {
                newChain.join(chain);
            }

            chains.removeAll(samePlayerChains);
            chains.add(newChain);

            Stone stone = new Stone((byte)x, (byte)y, (byte)player, (byte)liberties, newChain);
            board[y][x] = stone;
            newHash = zobristXor(newHash,x,y,player);

            for(Stone s : samePlayerStones)
                s.decLiberties();

            if(violatesKo(newHash)) {
                chains = oldChains;
                board = oldBoard;
                playerCaptures =oldPlayerCaptures;
                zobristIndices = oldZobristIndices;
                return false;
            }
            else {
                playerPassed[0] = false;
                playerPassed[1] = false;
                updateHashes(newHash);
                return true;
            }
        }
    }

    public boolean outOfBounds(int x, int y) {
        return x < 0 || x >= Parameters.boardSize || y < 0 || y >= Parameters.boardSize;
    }

    public boolean verifyMove(int x, int y, int player) {
      return !(outOfBounds(x, y) || board[y][x]!= null || violatesSuicide(x, y, player));
    }

    public boolean lightVerifyMove(int x, int y, int player) {
        return !(board[y][x]!= null || violatesSuicide(x, y, player));
    }

    public boolean violatesSuicide(int x, int y, int player) {
        Stone neighbor = null;
        Chain neighborChain = null;
        HashSet<Chain> chains = new HashSet<>(4);
        HashMap<Chain,Integer> enemyChains = new HashMap<>();
        int stoneCount = 0;

        for(int i=0; i<4 ; i++){
            switch(i){
                case 0:
                    if(x == 0)
                        continue;
                    neighbor = board[y][x-1];
                    break;
                case 1:
                    if(x == Parameters.boardSize - 1)
                        continue;
                    neighbor = board[y][x+1];
                    break;
                case 2:
                    if(y == 0)
                        continue;
                    neighbor = board[y-1][x];
                    break;
                case 3:
                    if(y == Parameters.boardSize - 1)
                        continue;
                    neighbor = board[y+1][x];
                    break;
            }

            if(neighbor == null)
                return false;

            neighborChain = neighbor.getChain();

            if(neighbor.getPlayer() == player) {
                chains.add(neighborChain);
                stoneCount++;
            }
            else {
              if(!enemyChains.containsKey(neighborChain))
                  enemyChains.put(neighborChain, (int)neighborChain.getLiberties()-1);
              else
                  enemyChains.put(neighborChain, enemyChains.get(neighborChain)-1);
            }
        }

        if(stoneCount != 0){
            int liberties = 0;
            for(Chain c : chains)
                liberties += c.getLiberties();

            if(liberties != stoneCount)
                return false;
        }

        if(enemyChains.containsValue(0))
            return false;

        return true;
    }

    public boolean violatesKo(long newHash) {
        if(newHash == prevZobristHash)
            return true;
        else
            return false;
    }

    private void updateSurroundings(ArrayList<Stone> capturedStones) {
        Stone neighbor = null;
        int x,y;
        for(Stone stone : capturedStones) {
            x = stone.getX();
            y = stone.getY();
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        if (x == 0)
                            continue;
                        neighbor = board[y][x - 1];
                        break;
                    case 1:
                        if (x == Parameters.boardSize - 1)
                            continue;
                        neighbor = board[y][x + 1];
                        break;
                    case 2:
                        if (y == 0)
                            continue;
                        neighbor = board[y - 1][x];
                        break;
                    case 3:
                        if (y == Parameters.boardSize - 1)
                            continue;
                        neighbor = board[y + 1][x];
                        break;
                }

                if(neighbor != null)
                    neighbor.incLiberties();

            }
        }
    }
    public int checkSpace(int xPos, int yPos){
        return board[yPos][xPos] == null ? 0 : board[yPos][xPos].getPlayer();
    }

    public int getPlayerCaptures(int player) {
        return playerCaptures[player-1];
    }

    public void pass(int player){
        playerPassed[player-1] = true; //TODO: Resetear si en el turno siguiente no pasa
    }
    
    public boolean passed(int player){
        return playerPassed[player-1];
    }

    public boolean gameFinished(){
        //falta game finished

        return playerPassed[0] && playerPassed[1];
    }

    /**
     * Calculates territory held by both players. It does not return individual territory
     * because the scores are always sought in pairs, so the most efficient solution is to calculate them both.
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
    public int[] calculateTerritory(){
        Board mockBoard = new Board(); // 0 is unvisited, 1 is visited

        // First array: 0 is out of board, 1 is player 1, 2 is player 2
        // Second array: Set cardinal = spaces counted
        ArrayList<ArrayList<Integer>> borders = new ArrayList<>();
        borders.add(new ArrayList<Integer>());
        borders.add(new ArrayList<Integer>());

        int[] points = {0,0}; // return parameter: First element represents player 1's points, second element represents player 2's points.
        boolean player1=false;
        boolean player2=false;

        for (int i = 0; i< Parameters.boardSize; i++){
            for (int j = 0; j< Parameters.boardSize; j++){
                if (mockBoard.checkSpace(i,j)==0)
                    floodFill(mockBoard,this,i,j,borders);

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
        if( xPos<0 || xPos>12 || yPos<0 || yPos>12){
            borders.get(0).add(0);
            return borders;
        }
        int rb = realBoard.checkSpace(xPos,yPos);
        if (mockBoard.checkSpace(xPos,yPos)==1) {// If space is already visited.
            if(rb!=0)
                borders.get(0).add(rb);
            return borders;
        }
        else {
            mockBoard.mockAddPiece(xPos, yPos, 1); // Mark as visited.
        }

        if (rb!=0){
            borders.get(0).add(rb);
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
        int[] territory = calculateTerritory();
        int winner;
        if ( (winner = territory[0]+playerCaptures[0] - (territory[1]+playerCaptures[1])) == 0)
            return winner;
        //System.out.println(winner);
        return winner > 0 ? 1 : 2;
    }

    //For debugging
    public String toString(){
        String ans = "";
        for(int y=0; y < 13; y++){
            ans+= "[";
            for(int x=0; x < 13; x++){
                ans+="   ";
                ans+=this.checkSpace(x,y);
            }
            ans+= "]\n";
        }
        return ans;
    }

    public String chainBoardAsString() {
        ArrayList<Chain> chainList = new ArrayList<>();
        chainList.addAll(chains);
        Stone s;
        String ans = "";
        for(int i=0;i<13;i++){
            ans+= "[";
            for(int j=0;j<13;j++){
                s = board[i][j];
                if(s != null)
                    ans += String.format("%1$4s", chainList.indexOf(s.getChain()));
                else
                    ans += "   -";
            }
            ans+= "]\n";
        }
        return ans;
    }


    public void initializeZobrist(){
        long bitString;
        zobristTable = new long[169][3];
        for(int i=0; i<169; i++){
            for(int j=0; j<3; j++){
                bitString =0;
                for(int p=0; p<63; p++) {
                    if(Math.random() > 0.5f)
                        bitString +=(long)Math.pow(2, p);

                }
                zobristTable[i][j]= bitString;
            }
        }
    }
    /**Esta funcion rehashea la tabla a partir de un solo cambio
     * */
    public long zobristXor(long oldHash, int x, int y, int player){
        int index = y*13 +x;
        if(index<0 || index>169)
            throw new IllegalArgumentException("wrong x or y");
        long newHash;
        if(player == 0){
            newHash = oldHash ^ zobristTable[index][zobristIndices.get(index)];
            zobristIndices.set(index,0);
            newHash = newHash ^ zobristTable[index][zobristIndices.get(index)];
        }
        else if(player == 1){
            newHash = oldHash ^ zobristTable[index][zobristIndices.get(index)];
            zobristIndices.set(index,1);
            newHash = newHash ^ zobristTable[index][zobristIndices.get(index)];
        }
        else if(player == 2){
            newHash = oldHash ^ zobristTable[index][zobristIndices.get(index)];
            zobristIndices.set(index,2);
            newHash = newHash ^ zobristTable[index][zobristIndices.get(index)];
        }
        else
            throw new IllegalArgumentException("wrong player n");

        oldHash = newHash;
        return oldHash;
    }


    public void updateHashes(long newHash){
        prevZobristHash = zobristHash;
        zobristHash = newHash;
    }

    public long zobristHash(){
        long hash=0;
        for(int y=0; y<13; y++){
            for(int x=0; x<13; x++){
                if(board[y][x] == null) {
                    zobristIndices.add(0);
                    continue;
                }
                if(board[y][x].getPlayer() == 1){
                    zobristIndices.add(1);
                    continue;
                }
                if(board[y][x].getPlayer() == 2){
                    zobristIndices.add(2);
                    continue;
                }
            }
        }

        for(int i=0;i<169;i++){
            hash = hash ^ zobristTable[i][zobristIndices.get(i)];
        }
        return hash;
    }
}

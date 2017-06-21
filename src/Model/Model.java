package Model;

import Controller.Controller;

import java.util.Random;

/**
 * Created by juan on 23/05/17.
 */
public class Model {

    private Board board;
    private int AIplayer;


    public Model(Board board, int AIplayer){
        if(board ==null)
            throw new NullPointerException("Board can't be null");
        if(AIplayer != 1 && AIplayer != 2)
            throw new IllegalArgumentException("AIplayer must be 1 or 2");

        this.board = board;
        this.AIplayer = AIplayer;
    }

    public Board getBoard() {
        return board;
    }

    public boolean addPiece(int x, int y, int player) {
        if(player != AIplayer && player == board.getPlayerN() && board.addPiece(x, y, player)) {
            board.nextPlayer();
            return true;
        }
        return false;
    }

    public boolean pass(int player) {
        if(player != AIplayer && player == board.getPlayerN()) {
            board.pass(player);
            board.nextPlayer();
            return true;
        }
        return false;
    }

    public static void koTest1() {
        Board board = new Board();
        Random rand = new Random();
        long from = System.currentTimeMillis();
        long to = 3000;
        board.addPiece(1,9,1);
        board.addPiece(1,10,2);
        board.addPiece(2,8,1);
        board.addPiece(2,9,2);
        board.addPiece(2,11,2);
        board.addPiece(3,9,1);
        board.addPiece(3,10,2);
        Controller.updateView(board);

        while(System.currentTimeMillis()-from < to){}

        board.addPiece(2,10,1);
        Controller.updateView(board);

        from = System.currentTimeMillis();
        while(System.currentTimeMillis()-from < to){}

        board.addPiece(2,9,2);
        Controller.updateView(board);
    }


    public static void koTest2() {
        Board board = new Board();
        Random rand = new Random();
        long from = System.currentTimeMillis();
        long to = 3000;
        board.addPiece(0,12,2);
        board.addPiece(1,11,2);
        board.addPiece(2,12,2);
        board.addPiece(2,11,1);
        board.addPiece(3, 12,1);
        Controller.updateView(board);

        while (System.currentTimeMillis() - from < to) {
        }

        board.addPiece(1, 12, 1);
        Controller.updateView(board);

        from = System.currentTimeMillis();
        while (System.currentTimeMillis() - from < to) {
        }

        board.addPiece(2, 12, 2);
        Controller.updateView(board);
    }


    public static void koTest3() {
        Board board = new Board();
        Random rand = new Random();
        long from = System.currentTimeMillis();
        long to = 3000;
        board.addPiece(2,11,2);
        board.addPiece(3,10,2);
        board.addPiece(4,10,2);
        board.addPiece(4,11,2);
        board.addPiece(3,12,2);
        board.addPiece(2,12,1);
        board.addPiece(4,12,1);
        board.addPiece(5,12,1);

        Controller.updateView(board);

        while (System.currentTimeMillis() - from < to) {
        }

        board.addPiece(3, 11, 1);
        Controller.updateView(board);

        from = System.currentTimeMillis();
        while (System.currentTimeMillis() - from < to) {
        }

        board.addPiece(3, 12, 2);
        Controller.updateView(board);
    }

    public static void koTest4() {
        Board board = new Board();
        Random rand = new Random();
        long from = System.currentTimeMillis();
        long to = 3000;
        board.addPiece(11,10,1);
        board.addPiece(12,10,1);
        board.addPiece(11,11,1);
        board.addPiece(12,11,2);
        board.addPiece(10,12,2);
        board.addPiece(11,12,2);


        Controller.updateView(board);

        while (System.currentTimeMillis() - from < to) {
        }

        board.addPiece(12, 12, 1);
        Controller.updateView(board);

        from = System.currentTimeMillis();
        while (System.currentTimeMillis() - from < to) {
        }

        board.addPiece(12, 11, 2);
        Controller.updateView(board);
    }

    public void gameLoop(){

        AI ai = new AI(AIplayer);

        while(!board.gameFinished()){
            try {
                Thread.sleep(10);
            }
            catch (Exception e){
                continue;
            }

            if(board.getPlayerN() == AIplayer) {
                board = ai.getMove(board).board;
                board.nextPlayer();
                Controller.updateView(board);
            }

        }
        int winner = board.calculateWinner();
        Controller.setWinnerView(winner);
        System.out.println("winner is: "+winner);
    }

    public void executeFileMode(){
        AI ai = new AI(AIplayer);
        System.out.println(ai.getMove(board));
    }

}

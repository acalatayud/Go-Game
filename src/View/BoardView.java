package View;

import Model.Board;
import Model.Model;
import Service.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by juan on 23/05/17.
 */
public class BoardView {
    public static void main(String[] args){
        Board test = new Board();
        test.addPiece(1,0,1);
        test.addPiece(1,2,1);
        test.addPiece(1,1,1);
        test.addPiece(0,3,1);
        test.addPiece(4,7,2);
        System.out.println(test);
        System.out.println("Player 1 holds " + Model.calculateTerritory(test)[0] + " spaces.\nPlayer 2 holds " + Model.calculateTerritory(test)[1]+" spaces.");
        System.out.println("The winner is: " + test.calculateWinner());
        //BoardView boardview = new BoardView();
    }
    public BoardView() {
        initialize();
    }
    public void initialize(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel();
        JPanel containerPanel = new JPanel();

        buttonPanel.setLayout(new GridLayout(Constants.boardSize, Constants.boardSize));
        for (int i = 0; i< Constants.boardSize* Constants.boardSize; i++) {
            buttonPanel.add(new JButton("" + (i+1)));
        }

        buttonPanel.setPreferredSize(new Dimension(800, 800));
        containerPanel.add(buttonPanel);

        frame.getContentPane().add(containerPanel);
        frame.pack();
        frame.setVisible(true);
    }
}

package View;

import Service.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by juan on 23/05/17.
 */
public class BoardView {
    public static void main(String[] args){
        BoardView boardview = new BoardView();
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

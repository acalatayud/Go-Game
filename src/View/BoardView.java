package View;

import javax.swing.*;
import java.awt.*;

/**
 * Created by juan on 23/05/17.
 */
public class Board {
    public static void main(String[] args){
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel buttonPanel = new JPanel();
            JPanel containerPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(12,12));
            for (int i=0;i<12*12;i++) {
                buttonPanel.add(new JButton("" + i));
            }
            buttonPanel.setPreferredSize(new Dimension(800, 800));
            containerPanel.add(buttonPanel);

            frame.getContentPane().add(containerPanel);
            frame.pack();
            frame.setVisible(true);
    }
    public Board() {
        JButton button = new JButton();
        button.setBounds(0, 1, 10, 10);
        button.setBackground(Color.orange);
        this.add(button);

        this.setTitle("Go Board");
        this.setSize(1000,1000);
        this.setVisible(true);
    }
}

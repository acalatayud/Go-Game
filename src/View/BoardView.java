package View;

import Model.Board;
import Model.Model;
import Service.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by juan on 23/05/17.
 */
public class BoardView {
/*
    private class ButtonListener{
        public void actionPerformed(ActionEvent e){
            int i = 0;
            Component[] comp = stoneButtonsPanel.getComponents();
            while(e.getSource()!= (JButton)comp[i]) {
                i++;
            }
            if(e.getSource() ==(JButton)comp[i]){
                JButton b = (JButton)comp[i];
                setPostition(i);
            }
        }
    }

    public BoardView() {
        initialize();
    }
    public void initialize(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel StoneButtonsPanel = new JPanel();
        JPanel containerPanel = new JPanel();

        StoneButtonsPanel.setLayout(new GridLayout(Constants.boardSize, Constants.boardSize));
        for (int i = 0; i< Constants.boardSize* Constants.boardSize; i++) {
            JButton b = new JButton("" + (i));
            ButtonListener bl = new ButtonListener();
            b.addActionListener(bl);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            StoneButtonsPanel.add(b);

        }

        StoneButtonsPanel.setPreferredSize(new Dimension(800, 800));
        containerPanel.add(StoneButtonsPanel);

        frame.getContentPane().add(containerPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void buttonClick(){}

    public boolean setPostition(int color, int pos){
        JButton b;
        Component[] comp = stoneButtonsPanel.getComponents();
        if(comp[pos] instanceof JButton ){
            b = (JButton)comp[pos];
            if(color ==0){
                b.setOpaque(false);
                b.setContentAreaFilled(false);
            }
            else if(color == 1) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(Color.black);
            }
            else if(color == 2) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(Color.white);
            }
            else throw new IllegalParameterException("color must be 0(empty cell), 1(black) or 2(white)");
            return true;
        }
        return false;
    }
    */
}

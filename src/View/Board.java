package View;

import javax.swing.*;
import java.awt.*;

/**
 * Created by juan on 23/05/17.
 */
public class Board extends JFrame {
    public static void main(String[] args){
        Board board = new Board();
    }
    public Board(){

        this.setBackground(Color.orange);
        this.setTitle("Go Board");
        this.setLayout(new GridLayout(8,8));
        this.setSize(1000,1000);
        this.setVisible(true);
    }
}

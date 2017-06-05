package View;
import Controller.Controller;
import Model.Board;
import Model.Stone;
import Service.Constants;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.*;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BoardView {
    private boolean clickAvailable = true;
    private JFrame frame;
    int playerN;
    ImageIcon blackStone = new ImageIcon("src/Sources/blackStone20.png");
    ImageIcon whiteStone = new ImageIcon("src/Sources/whiteStone20.png");
    private JTextArea txtPlayern;
    private ImagePanel stoneButtonsPanel;
    private JPanel bottomPanel;
    private JButton btnPass;
    private StoneButton[][] stoneButtons;

    /**
     * Create the application and initialize it.
     */
    public BoardView() {
        stoneButtons = new StoneButton[Constants.boardSize][Constants.boardSize];
        frame = new JFrame();
        frame.setResizable(false);
        frame.setBounds(300, 300, 610, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

        JPanel containerPanel = new JPanel();
        frame.getContentPane().add(containerPanel);
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        containerPanel.setLayout(gbl);
        containerPanel.setSize(600, 650);

        Image bg = Toolkit.getDefaultToolkit().createImage("Sources/Board600.png");
        stoneButtonsPanel = new ImagePanel(bg);
        stoneButtonsPanel.setSize(600,600);
        Dimension dim = new Dimension(600,600);
        stoneButtonsPanel.setPreferredSize(dim);
        stoneButtonsPanel.setMaximumSize(dim);
        stoneButtonsPanel.setMinimumSize(dim);
        c.gridx = 0;
        c.gridy = 0;
        CreateButtons();
        stoneButtonsPanel.setLayout(new GridLayout(13, 13, 0, 0));
        containerPanel.add(stoneButtonsPanel,c);


        bottomPanel = new JPanel();
        bottomPanel.setSize(600, 50);
        dim = new Dimension(600, 50);
        bottomPanel.setMinimumSize(dim);
        bottomPanel.setMaximumSize(dim);
        bottomPanel.setPreferredSize(dim);
        FlowLayout fl_bottomPanel = (FlowLayout) bottomPanel.getLayout();
        fl_bottomPanel.setAlignment(FlowLayout.LEFT);
        c.gridx = 0;
        c.gridy = 1;
        containerPanel.add(bottomPanel, c);


        btnPass = new JButton("pass");
        btnPass.addActionListener(new ButtonListener());
        bottomPanel.add(btnPass);

        txtPlayern = new JTextArea();
        txtPlayern.setText("Player 1");
        playerN = 1;
        bottomPanel.add(txtPlayern);
        txtPlayern.setColumns(10);

    }

    /**
     * same as the above but with no board.
     */
//    public BoardView(){
//
//        stoneButtons = new ArrayList<>();
//        frame = new JFrame();
//        frame.setResizable(false);
//        frame.setBounds(300, 300, 610, 680);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
//
//        JPanel containerPanel = new JPanel();
//        frame.getContentPane().add(containerPanel);
//        GridBagLayout gbl = new GridBagLayout();
//        GridBagConstraints c = new GridBagConstraints();
//        containerPanel.setLayout(gbl);
//        containerPanel.setSize(600, 650);
//
//        Image bg = Toolkit.getDefaultToolkit().createImage("Sources/Board600.png");
//        stoneButtonsPanel = new ImagePanel(bg);
//        stoneButtonsPanel.setSize(600,600);
//        Dimension dim = new Dimension(600,600);
//        stoneButtonsPanel.setPreferredSize(dim);
//        stoneButtonsPanel.setMaximumSize(dim);
//        stoneButtonsPanel.setMinimumSize(dim);
//        c.gridx = 0;
//        c.gridy = 0;
//        stoneButtonsPanel.setLayout(new GridLayout(13, 13, 0, 0));
//        containerPanel.add(stoneButtonsPanel,c);
//
//
//        bottomPanel = new JPanel();
//        bottomPanel.setSize(600, 50);
//        dim = new Dimension(600, 50);
//        bottomPanel.setMinimumSize(dim);
//        bottomPanel.setMaximumSize(dim);
//        bottomPanel.setPreferredSize(dim);
//        FlowLayout fl_bottomPanel = (FlowLayout) bottomPanel.getLayout();
//        fl_bottomPanel.setAlignment(FlowLayout.LEFT);
//        c.gridx = 0;
//        c.gridy = 1;
//        containerPanel.add(bottomPanel, c);
//
//        placeStones(null);
//
//        btnPass = new JButton("pass");
//        btnPass.addActionListener(new ButtonListener());
//        bottomPanel.add(btnPass);
//
//        playerN = 1;
//        txtPlayern.setText("player 1");
//        bottomPanel.add(txtPlayern);
//        txtPlayern.setColumns(10);
//    }

    /**Auxiliary method to place a board´s stones within the swing environment.
     * */
    public void update(Board board){
        playerN = board.getPlayerN();
        int y=0;
        int x;
        if(board == null)
        throw new IllegalArgumentException("board is null");
        for (Stone[] row : board.getBoard()) {
            x =0;
            for (Stone s : row) {
                StoneButton b =  stoneButtons[x][y];
                if (s != null) {
                    setStone(b,s.getPlayer());
                }
                else{
                    removeStone(b);
                }
                x++;
            }
            y++;
        }
    }

    private void CreateButtons(){
        int x, y;
        for (y = 0; y < 13; y++) {
            for(x=0; x < 13; x++) {
                StoneButton b = new StoneButton(x, y);
                ButtonListener bl = new ButtonListener();
                b.addActionListener(bl);
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                stoneButtons[x][y]= b;
                stoneButtonsPanel.add(b);
            }
        }
    }

    /**Class extension to support background rendering.
     * */
    private class ImagePanel extends JPanel{

        private static final long serialVersionUID = 1L;
        private Image image = null;
        private int iWidth2;
        private int iHeight2;

        public ImagePanel(Image image)
        {
            this.image = image;
            this.iWidth2 = image.getWidth(this)/2;
            this.iHeight2 = image.getHeight(this)/2;
        }


        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (image != null)
            {
                int x = iWidth2;
                int y = iHeight2;
                g.drawImage(image,x,y,this);
            }
        }
    }

    /**Class extension to support background rendering.
     * */
    private class StoneButton extends JButton{
        int row;
        int col;
        public StoneButton(int i, int j){
            super();
            row = i;
            col = j;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    /**Button listener which event triggers are handled by the controller.
     * */
    private class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if (clickAvailable) {
                clickAvailable = false;
                if (e.getSource() == btnPass) {
                    Controller.pass();
                    return;
                }
                if (e.getSource() instanceof StoneButton) {
                    StoneButton button = (StoneButton) e.getSource();
                    int row = button.getRow();
                    int col = button.getCol();
                    Controller.placingAttempt(row, col,playerN);

                }
                clickAvailable = true;
            }
        }

    }

    /**Updates the player to transition from one player to the next.
     * */
//    public void nextPlayer(){
//
//        if(playerN == 1) {
//            playerN = 2;
//            txtPlayern.setText("player 2");
//            clickAvailable = false;
//        }
//        else {
//            playerN = 1;
//            txtPlayern.setText("player 1");
//            clickAvailable = true;
//        }
//
//    }

    /**Auxiliary function to initialize the frame.
    * */
    public void initFrame(){
        frame.setVisible(true);
    }

    /**This method will remove a stone from a certain position
     * @return true if there was a stone to be removed and it was removed successfully, false otherwise.
     * */
    public void removeStone(StoneButton stone){
        stone.setIcon(null);
    }

    /** sets a stone on the indi    cated position.
     *@return true if the stone could be successfully placed, false otherwise.
    **/
    public boolean setStone(StoneButton stone, int player){

        if(player == 1) {
            stone.setIcon(blackStone);
        }
        else {
            stone.setIcon(whiteStone);
        }
        return true;

    }

    /**sets the current player within the view.
     *@param player: 1 if it´s the black player, 2 if it´s the white player.
     * */
//    private void setPlayer(int player){
//        if(player != 1 & player!= 2)
//            throw new IllegalArgumentException("wrong playerN");
//        if(player == 1){
//            playerN = 1;
//            txtPlayern.setText("player 1");
//        }
//        else {
//            playerN = 2;
//            txtPlayern.setText("player 2");
//        }
//    }

}
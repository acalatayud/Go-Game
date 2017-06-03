package View;
import Controller.Controller;
import Model.Board;
import Model.Stone;

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
    ImageIcon blackStone = new ImageIcon("Sources/blackStone20.png");
    ImageIcon whiteStone = new ImageIcon("Sources/whiteStone20.png");
    private JTextArea txtPlayern;
    private ImagePanel stoneButtonsPanel;
    private JPanel bottomPanel;
    private JButton btnPass;
    private ArrayList<StoneButton> stoneButtons;

    /**
     * Create the application and initialize it.
     * @param board: a board can be passed which will initialize the swing components with the board´s current state.
     */
    public BoardView(Board board) {
        stoneButtons = new ArrayList<>();
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

        placeStones(board);

        btnPass = new JButton("pass");
        btnPass.addActionListener(new ButtonListener());
        bottomPanel.add(btnPass);

        txtPlayern = new JTextArea();
        playerN = board.getPlayerN();
        if(playerN == 1)
            txtPlayern.setText("Player 1");
        else
            txtPlayern.setText("Player 2");
        bottomPanel.add(txtPlayern);
        txtPlayern.setColumns(10);

    }

    /**
     * same as the above but with no board.
     */
    public BoardView(){

        stoneButtons = new ArrayList<>();
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

        placeStones(null);

        btnPass = new JButton("pass");
        btnPass.addActionListener(new ButtonListener());
        bottomPanel.add(btnPass);

        playerN = 1;
        txtPlayern.setText("player 1");
        bottomPanel.add(txtPlayern);
        txtPlayern.setColumns(10);
    }

    /**Auxiliary method to place a board´s stones within the swing environment.
     * */
    public void placeStones(Board board){
        if(board != null) {
            for (Stone[] row : board.getBoard()) {
                for (Stone s : row) {

                    StoneButton b = new StoneButton();
                    ButtonListener bl = new ButtonListener();
                    b.addActionListener(bl);
                    b.setOpaque(false);
                    b.setContentAreaFilled(false);
                    b.setBorderPainted(false);

                    if (s != null) {
                        b.setPlaced(true);
                        if (s.getPlayer() == 1)
                            b.setIcon(blackStone);
                        if (s.getPlayer() == 2)
                            b.setIcon(whiteStone);
                    }
                    stoneButtons.add(b);
                    stoneButtonsPanel.add(b);
                }
            }
        }
        else {
            Integer i;
            for (i = 0; i < 169; i++) {
                StoneButton b = new StoneButton();
                ButtonListener bl = new ButtonListener();
                b.addActionListener(bl);
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                stoneButtons.add(b);
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
        boolean placed;
        public StoneButton(){
            super();
            placed = false;
        }

        public void setPlaced(boolean val){
            placed = val;
        }
        private boolean isPlaced(){return placed;}
    }

    /**Button listener which event triggers are handled by the controller.
     * */
    private class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if (clickAvailable) {
                if (e.getSource() == btnPass) {
                    Controller.pass();
                    return;
                }
                int i = 0;
                while (e.getSource() != stoneButtons.get(i)) {
                    i++;
                }
                int fil = (int) Math.floor(i / 13);
                int col = (i - (fil * 13));
                clickAvailable = false;
                Controller.placingAttempt(fil, col, playerN);
                clickAvailable = true;
            }
        }

    }

    /**Updates the player to transition from one player to the next.
     * */
    public void nextPlayer(){

        if(playerN == 1) {
            playerN = 2;
            txtPlayern.setText("player 2");
        }
        else {
            playerN = 1;
            txtPlayern.setText("player 1");
        }

    }

    /**Auxiliary function to initialize the frame.
    * */
    public void initFrame(){
        frame.setVisible(true);
    }

    /**This method will remove a stone from a certain position
     * @param pos: the position from which the stone will be removed, the parameter must belong to the interval [0,168].
     * @return true if there was a stone to be removed and it was removed successfully, false otherwise.
     * */
    public boolean removeStone(int pos){
        if(pos<0 || pos>=169)
            throw new IllegalArgumentException("position must be a number from 0 to 168");
        StoneButton stone = stoneButtons.get(pos);
        if(stone.isPlaced()){
            stone.setPlaced(false);
            return true;
        }
        else
            return false;
    }

    /** sets a stone on the indicated position.
     *@param pos: the position from which the stone will be placed, the parameter must belong to the interval [0,168].
     *@return true if the stone could be successfully placed, false otherwise.
    **/
    public boolean setStone(int pos){

        StoneButton stone = stoneButtons.get(pos);
        if(stone.isPlaced())
            throw new IllegalArgumentException("the position is already occupied by a stone");
        else{
            if(playerN == 1) {
                stone.setIcon(blackStone);
                stone.setPlaced(true);
            }
            else {
                stone.setIcon(whiteStone);
                stone.setPlaced(true);
            }
            return true;
        }
    }

    /**sets the current player within the view.
     *@param player: 1 if it´s the black player, 2 if it´s the white player.
     * */
    private void setPlayer(int player){
        if(player != 1 & player!= 2)
            throw new IllegalArgumentException("wrong playerN");
        if(player == 1){
            playerN = 1;
            txtPlayern.setText("player 1");
        }
        else {
            playerN = 2;
            txtPlayern.setText("player 2");
        }
    }

}
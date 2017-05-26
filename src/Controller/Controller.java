package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Model.Board;

/**
 * Created by juan on 23/05/17.
 */
public class Controller {
    public static Board waitForPlayerMove(Board board){
        return new Board();//dummy para q compile
    }

    public static Board readBoard(File file){
      Board board = new Board();
    	int c;
    	int xPos = 0;
    	int yPos = 0;

    	try {
    		FileInputStream fileIn = new FileInputStream(file);
    		while ((c = fileIn.read()) != -1) {
    			if(c == '\n'){
    				yPos++;
    				xPos = 0;
    			}
    			else {
    				if(c != ' ')
    					board.addPiece(xPos, yPos, c=='1');
    				xPos++;
    			}
    		}
				fileIn.close();

    	} catch (IOException i) {
    		board = null;
    	}

    	return board;
    }
}

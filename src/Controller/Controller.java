package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import Model.Board;
import Model.Model;
import Service.Constants;

/**
 * Created by juan on 23/05/17.
 */
public class Controller {
	public static void main(String[] args){
		int visual;
		int file;
		int playerN;
		int tree;
		ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
		try {
			if (argsList.size() < 3)
				throw new IllegalArgumentException();
			else {
				visual = argsList.indexOf("-visual");
				file = argsList.indexOf("-file");

				if ((visual == -1 && file == -1) || (visual != -1 && file != -1)) {
					throw new IllegalArgumentException();
				}

				if (file!=-1){
					String filename = argsList.get(file+1);
					File fileHandler = new File(filename);
					if(!fileHandler.exists())
						throw new FileNotFoundException();
					Board startingBoard = readBoard(fileHandler);
					if(startingBoard == null)
						throw new IOException();
				}

				int player = argsList.indexOf("-player");
				playerN=0;

				if ((player != -1 && file == -1) || (file != -1 && player == -1)){
					throw new IllegalArgumentException();
				}

				if (player!=-1){
					try {
						playerN = Integer.parseInt(argsList.get(player + 1));
						if (playerN!=1&&playerN!=2)
							throw new NumberFormatException();
					}
					catch (NumberFormatException e){
						throw new IllegalArgumentException();
					}
				}

				int maxtime = argsList.indexOf("-maxtime");
				int maxtimeN=0;
				int depth = argsList.indexOf("-depth");
				int depthN=0;

				if ((maxtime != -1 && depth != -1) || (maxtime == -1 && depth == -1)){
					throw new IllegalArgumentException();
				}

				if(maxtime!=-1){
					try {
						maxtimeN = Integer.parseInt(argsList.get(maxtime + 1));
						if (maxtimeN<=0)
							throw new NumberFormatException();
						Constants.maxTime = maxtimeN;
					}
					catch (NumberFormatException e){
						throw new IllegalArgumentException();
					}
				}

				if(depth!=-1){
					try {
						depthN = Integer.parseInt(argsList.get(depth + 1));
						if(depthN<=0)
							throw new NumberFormatException();
						Constants.depth = depthN;
					}
					catch (NumberFormatException e){
						throw new IllegalArgumentException();
					}
				}

				int prune = argsList.indexOf("-prune");
				tree = argsList.indexOf("-tree");

				if (prune!=-1)
					Constants.prune = true;

				if(tree!=-1)
					tree=1;
			}
		}
		catch (IllegalArgumentException e){
			System.out.println("Invalid parameters, try:\njava -jar tpe.jar (-visual | -file archivo -player n) (-maxtime n | -depth n) [-prune] [-tree]\n");
			return;
		}
		catch (IOException e){
			System.out.println("Invalid file\n");
			return;
		}

		// Parsed values:
		//     visual : -1 is false, !=-1 is true
		//     file : -1 is false, !=-1 is true
		//     playerN : 1 or 2 representing the players turn
		//     maxtimeN : Saved in constants
		//     depthN : Saved in constants
		//     prune : Saved in constants
		//     tree : -1 is false, !=-1 is true

		Model model = new Model();
		Board board = new Board();

		if (visual == 1)
			model.gameLoop(board,playerN,tree==1);
		else{
			model.executeFileMode(board,playerN);
		}


	}
    public static Board waitForPlayerMove(Board board){
        return new Board();//dummy para q compile
        
        // cuando el jugador pasa devuelve null
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
    					board.addPiece(xPos, yPos, c-'0');
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

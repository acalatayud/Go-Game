package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import Model.Board;
import Service.Constants;

/**
 * Created by juan on 23/05/17.
 */
public class Controller {
	public static void main(String[] args){
		ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
		try {
			if (argsList.size() < 3)
				throw new IllegalArgumentException();
			else {
				int visual = argsList.indexOf("-visual");
				int file = argsList.indexOf("-file");

				if ((visual == -1 && file == -1) || (visual != -1 && file != -1)) {
					throw new IllegalArgumentException();
				}

				if (file!=-1){
					String filename = argsList.get(file+1);
				}

				int player = argsList.indexOf("-player");
				int playerN=0;

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
						Constants.maxTime = maxtimeN;
					}
					catch (NumberFormatException e){
						throw new IllegalArgumentException();
					}
				}

				if(depth!=-1){
					try {
						depthN = Integer.parseInt(argsList.get(depth + 1));
						Constants.depth = depthN;
					}
					catch (NumberFormatException e){
						throw new IllegalArgumentException();
					}
				}

				int prune = argsList.indexOf("-prune");
				int tree = argsList.indexOf("-tree");

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
		// Parsed values:
		//     visual : -1 is false, !=-1 is true
		//     file : -1 is false, !=-1 is true
		//     playerN : 1 or 2 representing the players turn
		//     maxtimeN : Saved in constants
		//     depthN : Saved in constants
		//     prune : Saved in constants
		//     tree : -1 is false, !=-1 is true

		//Call gameLoop or file functions

	}
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
    					board.addPiece(xPos, yPos, c);
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

/*
			if(args[0].equals("-visual")||args[0].equals("-file")) {
				int index=0;
				if (args[0].equals("-visual")) {
					// do visual
				} else{
					//do file
				}
				if(args[index].equals("-maxtime")||args[index].equals("-depth")){
					if(args[index].equals("-maxtime")){
						//do maxtime
					}
					else{
						//do depth
					}

				}
				else{
					throw new IllegalArgumentException("Need -maxtime or -depth, try:\njava -jar tpe.jar (-visual | -file archivo -player n) (-maxtime n | -depth n) [-prune] [-tree]");
				}
			}
			else{
				throw new IllegalArgumentException("Need -visual or -file, try:\njava -jar tpe.jar (-visual | -file archivo -player n) (-maxtime n | -depth n) [-prune] [-tree]");
			}
			*/

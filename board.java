package ce326.hw3;

import java.util.ArrayList;

public class board {
    static int board[][];
    static int colPlayed[];
    static int player;
	static boolean gameEnded;
	static boolean AIOpp = true;
	static boolean playerFirst;
	static ArrayList<Integer> moveList;

	// The board constructor.
    public board () {
        board = new int [6][7];
        colPlayed = new int [7]; 
		if (app.rad2.isSelected()) {
			player = 2;
			playerFirst = false;
		}
		else {
			player = 1;
			playerFirst = true;
		}
		
		moveList = new ArrayList<Integer>();

		gameEnded = false;
    }

	// Make the move in the board.
    public static void move (int col) {
        if (colPlayed[col] == 6) {
            app.makeWindow("Column is full");
			return;
        }
        
        board[colPlayed[col]][col] = player;
        colPlayed[col] ++;
		app.determinePiece(col);

		moveList.add(col);

		if (isFull())
			app.makeWindow("Tie!");

		checkWin(colPlayed[col]-1, col);

		changePlayer();
    }

	// Check if the board is full.
	public static boolean isFull() {
		int check = 0;
		
		for (int col = 0; col < 7; col ++) {
			if (colPlayed[col] == 6)
				check ++;
		}

		if (check == 7)
			gameEnded = true;

		return ((check == 7) ? true : false);
	}

	// Change the current player.
	public static void changePlayer () {
		player = (player == 1) ? 2 : 1;
	}

	// Check if the AI must make a move.
	public static void checkAI () {
		if (gameEnded == false) {
			if (AIOpp == true) {
				if (player == 2) {
					int val = AIPlayer.calculateBestMove(AIPlayer.depth);
					// double val[] = AIPlayer.minimax(AIPlayer.depth, -Double.MAX_VALUE, Double.MAX_VALUE, true);

					move(val);
				}
			}
		}
	}

	// Check if the board has a winning state in it.
	public static void checkWin(int row, int col) {
		int player = board[row][col];
	
		// Check for a connect 4 horizontally.
		int checkWin = 0;
		int checkCol = col;
		while (checkCol > 0 && board[row][checkCol - 1] == player) {
			checkCol--;
		}
		for (int i = checkCol; i < Math.min(checkCol + 4, board[0].length); i++) {
			if (board[row][i] == player) {
				checkWin++;
			}
		}
		if (checkWin == 4) {
			gameEnded = true;
			fileSystem.makeFile();
			app.gameOverDialog();
			return;
		}
	
		// Check for a connect 4 vertically.
		int checkRow = row;
		checkWin = 0;
		while (checkRow > 0 && board[checkRow - 1][col] == player) {
			checkRow--;
		}
		for (int i = checkRow; i < Math.min(checkRow + 4, board.length); i++) {
			if (board[i][col] == player) {
				checkWin++;
			}
		}
		if (checkWin == 4) {
			gameEnded = true;
			app.gameOverDialog();
			fileSystem.makeFile();
			return;
		}
	 
		// Check for a connect 4 diagonally to the left.
		checkRow = row;
		checkCol = col;
		checkWin = 0;
		while (checkRow > 0 && checkCol > 0 && board[checkRow - 1][checkCol - 1] == player) {
			checkRow--;
			checkCol--;
		}
		while (checkRow < board.length && checkCol < board[0].length) {
			if (board[checkRow][checkCol] == player) {
				checkWin++;
				if (checkWin == 4) {
					gameEnded = true;
					fileSystem.makeFile();
					app.gameOverDialog();
					return;
				}
			} else {
				checkWin = 0;
			}
			checkRow++;
			checkCol++;
		}

		// Check for a connect 4 diagonally to the right.
		checkRow = row;
		checkCol = col;
		checkWin = 0;
		while (checkRow > 0 && checkCol < board[0].length - 1 && board[checkRow - 1][checkCol + 1] == player) {
			checkRow--;
			checkCol++;
		}
		while (checkRow < board.length && checkCol >= 0 && checkCol < board[0].length) {
			if (board[checkRow][checkCol] == player) {
				checkWin++;
				if (checkWin == 4) {
					gameEnded = true;
					fileSystem.makeFile();
					app.gameOverDialog();
					return;
				}
			} else {
				checkWin = 0;
			}
			checkRow++;
			checkCol--;
		}
	}
}
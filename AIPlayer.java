package ce326.hw3;
public class AIPlayer {
	static int depth = 1;

	// MinMax algorithm with alpha beta pruning.
	public static int minimaxAB(int depth, int alpha, int beta, boolean maximizingPlayer) {
		// If depth equals zero or board is full.
		if (depth == 0 || board.isFull()) {
			return evaluateScore();
		}
	
		// Maximizing player.
		if (maximizingPlayer) {
			int bestValue = Integer.MIN_VALUE;
			for (int col = 0; col < 7; col++) {
				if (board.colPlayed[col] == 6) {
					continue;
				}
				int row = board.colPlayed[col];
				board.board[row][col] = board.player;
				board.colPlayed[col]++;
	
				int value = minimaxAB(depth - 1, alpha, beta, false);

				board.colPlayed[col]--;
				board.board[row][col] = 0;
	
				bestValue = Math.max(bestValue, value);
				alpha = Math.max(alpha, bestValue);
				if (beta <= alpha) {
					break;
				}
			}
			return bestValue;
		// Minimizing player.
		} else {
			int bestValue = Integer.MAX_VALUE;
			for (int col = 0; col < 7; col++) {
				if (board.colPlayed[col] == 6) {
					continue;
				}
				int row = board.colPlayed[col];
				board.board[row][col] = (board.player == 1) ? 2 : 1;
				board.colPlayed[col]++;
	
				int value = minimaxAB(depth - 1, alpha, beta, true);
				
				board.colPlayed[col]--;
				board.board[row][col] = 0;
	
				bestValue = Math.min(bestValue, value);

				beta = Math.min(beta, bestValue);
				if (beta <= alpha) {
					break;
				}
			}
			return bestValue;
		}
	}

	// Calculate best move by calling the minmax algorithm from the opponent's point of view.
	public static int calculateBestMove(int depth) {
		int bestValue = -Integer.MAX_VALUE;
		int bestMove = -1;
		int moveValue;
	
		for (int col = 0; col < 7; col++) {
			if (board.colPlayed[col] == 6) {
				continue; // column is full, cannot play here
			}
			
			board.board[board.colPlayed[col]][col] = board.player;
			board.colPlayed[col]++;
	
			moveValue = minimaxAB(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
		
			board.colPlayed[col]--;
			board.board[board.colPlayed[col]][col] = 0;
			
			if (moveValue >= bestValue && 3 - col <= 3 - bestMove) {
				bestValue = moveValue;
				bestMove = col;
			}
		}
		
		return bestMove;
	}

	// Evaluate the total score of the board's situation.
    public static int evaluateScore() {
		int totalValue = 0;

		totalValue += calculateVerticalVal();
		totalValue += calculateHorizontalVal();
		totalValue += calculateRightDiagVal();
		totalValue += calculateLeftDiagVal();

        return (totalValue);
    }

	// Find the left diagonal possible quadraplets and calculate their scores.
	private static int calculateLeftDiagVal() {
		int col, row;
		int count = 0, oppCount = 0;
		int value = 0;

		// Check for every row that can have 4 up (diagonally).
		for (row = 0; row < 3; row ++) {
			// Check for every column that can have 4 left (diagonally).
			for (col = 3; col < 7; col ++) {
				// Check for 4 left diagonally.
				for (int i = 0; i < 4; i ++) {
					if (board.board[row+i][col-i] == 2) {
						if (oppCount > 0) {
							oppCount = 0;
							break;
						}

						count ++;
					}
					else if (board.board[row+i][col-i] != 0) {
						if (count > 0) {
							count = 0;
							break;
						}

						oppCount ++;
					}
				}

				value += calculateValue(count, oppCount);
				count = 0;
				oppCount = 0;
			}
		}

		return (value);
	}

	// Find the right diagonal possible quadraplets and calculate their scores.
	private static int calculateRightDiagVal() {
		int col, row;
		int count = 0, oppCount = 0;
		int value = 0;

		// Check every row that can have 4 up (diagonally).
		for (row = 0; row < 3; row ++) {
			// Check every column that can have 4 right (diagonally).
			for (col = 0; col < 4; col ++) {
				// Check for 4 right diagonally.
				for (int i = 0; i < 4; i ++) {
					if (board.board[row+i][col+i] == 2) {
						if (oppCount > 0) {
							oppCount = 0;
							break;
						}

						count ++;
					}
					else if (board.board[row+i][col+i] != 0) {
						if (count > 0) {
							count = 0;
							break;
						}

						oppCount ++;
					}
				}

				value += calculateValue(count, oppCount);
				count = 0;
				oppCount = 0;
			}
		}

		return (value);
	}

	// Find the horizontal possible quadraplets and calculate their scores.
	private static int calculateHorizontalVal() {
		int col, row;
		int count = 0, oppCount = 0;
		int value = 0;

		// Check all rows.
		for (row = 0; row < 6; row ++) {
			// Check every column that can have 4 horizontically.
			for (col = 0; col < 4; col ++) {
				// Check for 4 horizontal.
				for (int i = 0; i < 4; i ++) {
					if (board.board[row][col+i] == 2) {
						if (oppCount > 0) {
							oppCount = 0;
							break;
						}
						
						count ++;
					}
					else if (board.board[row][col+i] != 0) {
						if (count > 0) {
							count = 0;
							break;
						}

						oppCount ++;
					}
				}

				value += calculateValue(count, oppCount);
				oppCount = 0;
				count = 0;
			}
		}

		return (value);
	}

	// Find the vertical possible quadraplets and calculate their scores.
    private static int calculateVerticalVal() {
        int col, row;
        int count = 0, oppCount = 0;
		int value = 0;

		// Check all columns.
        for (col = 0; col < 7; col ++) {
            // Check every row that can have 4 vertically.
			for (row = 0; row < board.colPlayed[col] && row < 3; row ++) {
				// Check for 4 vertical.
				for (int i = 0; i < 4; i ++) {
                    if (board.board[row+i][col] == 2) {
                        if (oppCount > 0) {
							oppCount = 0;
							break;
						}		
					
						count ++;
                    }
                    else if (board.board[row+i][col] != 0) {
						if (count > 0) {
							count = 0;
							break;
						}
					
						oppCount ++;
                    }
                }

				value += calculateValue(count, oppCount);
				oppCount = 0;
				count = 0;
            }
        }

		return (value);
    }

	// For one possible connect 4 calculate the total score.
	private static int calculateValue (int count, int oppCount) {
		int value = 0;

		if (count > 0) {
			if (count == 1) 
				value = 1;
			else if (count == 2)
				value = 4;
			else if (count == 3)
				value = 16;
			else if (count == 4)
				value = 200000;
		}
		else if (oppCount > 0) {
			if (oppCount == 1)
				value = -1;
			else if (oppCount == 2)
				value = -4;
			else if (oppCount == 3)
				value = -16;
			else if (oppCount == 4)
				value = -200000;
		}
		
		return value;
	}
}
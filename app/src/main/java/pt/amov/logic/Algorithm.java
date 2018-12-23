package pt.amov.logic;

import java.util.List;


public class Algorithm implements Constants{

	public static Play getGoodMove(byte[][] gameBoard, int depth, byte tokenColor, int difficulty) {

		if (tokenColor == BLACK)
			return max(gameBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, tokenColor, difficulty).play;
		else
			return min(gameBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, tokenColor, difficulty).play;
	}

	private static AIPlay max(byte[][] gameBoard, int depth, int alpha, int beta, byte tokenColor, int difficulty) {
		if (depth == 0) {
			return new AIPlay(evaluate(gameBoard, difficulty), null);
		}

		List<Play> possibleMoves = Rule.getPossiblePlays(gameBoard, tokenColor);
		if (possibleMoves.size() == 0) {
			if (Rule.getPossiblePlays(gameBoard, (byte)-tokenColor).size() == 0) {
				return new AIPlay(evaluate(gameBoard, difficulty), null);
			}
			return min(gameBoard, depth, alpha, beta, (byte)-tokenColor, difficulty);
		}

		byte[][] tmp = new byte[BOARDSIZE][BOARDSIZE];
        for (int i = 0; i <  BOARDSIZE; i++)
            System.arraycopy(gameBoard[i], 0, tmp[i], 0, BOARDSIZE);
		int best = Integer.MIN_VALUE;
		Play play = null;

		for (int i = 0; i < possibleMoves.size(); i++) {
            alpha = Math.max(best, alpha);
            if(alpha >= beta){
                break;
            }
			Rule.move(gameBoard, possibleMoves.get(i), tokenColor);
			int value = min(gameBoard, depth - 1, Math.max(best, alpha), beta, (byte)-tokenColor, difficulty).mark;
			if (value > best) {
				best = value;
				play = possibleMoves.get(i);
			}
            for (int j = 0; j <  BOARDSIZE; j++)
                System.arraycopy(tmp[j], 0, gameBoard[j], 0, BOARDSIZE);
		}
		return new AIPlay(best, play);
	}

	private static AIPlay min(byte[][] gameBoard, int depth, int alpha, int beta, byte tokenColor, int difficulty) {
		if (depth == 0) {
			return new AIPlay(evaluate(gameBoard, difficulty), null);
		}

		List<Play> PossibleMoves = Rule.getPossiblePlays(gameBoard, tokenColor);
		if (PossibleMoves.size() == 0) {
			if (Rule.getPossiblePlays(gameBoard, (byte)-tokenColor).size() == 0) {
				return new AIPlay(evaluate(gameBoard, difficulty), null);
			}
			return max(gameBoard, depth, alpha, beta, (byte)-tokenColor, difficulty);
		}

		byte[][] tmp = new byte[BOARDSIZE][BOARDSIZE];
        for (int i = 0; i <  BOARDSIZE; i++)
            System.arraycopy(gameBoard[i], 0, tmp[i], 0, BOARDSIZE);
		int best = Integer.MAX_VALUE;
		Play play = null;

		for (int i = 0; i < PossibleMoves.size(); i++) {
            beta = Math.min(best, beta);
            if(alpha >= beta){
                break;
            }
			Rule.move(gameBoard, PossibleMoves.get(i), tokenColor);
			int value = max(gameBoard, depth - 1, alpha, Math.min(best, beta), (byte)-tokenColor, difficulty).mark;
			if (value < best) {
				best = value;
				play = PossibleMoves.get(i);
			}
            for (int j = 0; j <  BOARDSIZE; j++)
                System.arraycopy(tmp[j], 0, gameBoard[j], 0, BOARDSIZE);
		}
		return new AIPlay(best, play);
	}

	private static int evaluate(byte[][] gameBoard, int difficulty) {
		int whiteEvaluate = 0;
		int blackEvaluate = 0;
		switch (difficulty) {
			case 1:
				for (int i = 0; i < BOARDSIZE; i++) {
					for (int j = 0; j < BOARDSIZE; j++) {
						if (gameBoard[i][j] == WHITE) {
							whiteEvaluate += 1;
						} else if (gameBoard[i][j] == BLACK) {
							blackEvaluate += 1;
						}
					}
				}
				break;
			case 2:
				for (int i = 0; i < BOARDSIZE; i++) {
					for (int j = 0; j < BOARDSIZE; j++) {
						if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 5;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 5;
							}
						} else if (i == 0 || i == 7 || j == 0 || j == 7) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 2;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 2;
							}
						} else {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 1;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 1;
							}
						}
					}
				}
				break;
			case 3:
				for (int i = 0; i < BOARDSIZE; i++) {
					for (int j = 0; j < BOARDSIZE; j++) {
						if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 5;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 5;
							}
						} else if (i == 0 || i == 7 || j == 0 || j == 7) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 2;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 2;
							}
						} else {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 1;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 1;
							}
						}
					}
				}
				blackEvaluate = blackEvaluate * 2 + Rule.getPossiblePlays(gameBoard, BLACK).size();
				whiteEvaluate = whiteEvaluate * 2 + Rule.getPossiblePlays(gameBoard, WHITE).size();
				break;
		}
		return blackEvaluate - whiteEvaluate;
	}


}

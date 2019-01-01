package pt.amov.reversISEC.logic;

import java.util.List;


public class AiAlgorithm implements Constants{

	public static Play getPlay(byte[][] gameBoard, int depth, byte tokenColor, int difficulty) {

		if (tokenColor == BLACK)
			return max(gameBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, tokenColor, difficulty).play;
		else
			return min(gameBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, tokenColor, difficulty).play;
	}

	private static AiPlay max(byte[][] gameBoard, int depth, int alpha, int beta, byte tokenColor, int difficulty) {
		if (depth == 0) {
			return new AiPlay(difficultyCicle(gameBoard, difficulty), null);
		}

		List<Play> possiblePlays = GameRules.getPossiblePlays(gameBoard, tokenColor);
		if (possiblePlays.size() == 0) {
			if (GameRules.getPossiblePlays(gameBoard, (byte)-tokenColor).size() == 0) {
				return new AiPlay(difficultyCicle(gameBoard, difficulty), null);
			}
			return min(gameBoard, depth, alpha, beta, (byte)-tokenColor, difficulty);
		}

		byte[][] tmp = new byte[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            System.arraycopy(gameBoard[i], 0, tmp[i], 0, BOARD_SIZE);
		int best = Integer.MIN_VALUE;
		Play play = null;

		for (int i = 0; i < possiblePlays.size(); i++) {
            alpha = Math.max(best, alpha);
            if(alpha >= beta){
                break;
            }
			GameRules.plays(gameBoard, possiblePlays.get(i), tokenColor);
			int value = min(gameBoard, depth - 1, Math.max(best, alpha), beta, (byte)-tokenColor, difficulty).mark;
			if (value > best) {
				best = value;
				play = possiblePlays.get(i);
			}
            for (int j = 0; j < BOARD_SIZE; j++)
                System.arraycopy(tmp[j], 0, gameBoard[j], 0, BOARD_SIZE);
		}
		return new AiPlay(best, play);
	}

	private static AiPlay min(byte[][] gameBoard, int depth, int alpha, int beta, byte tokenColor, int difficulty) {
		if (depth == 0) {
			return new AiPlay(difficultyCicle(gameBoard, difficulty), null);
		}

		List<Play> PossibleMoves = GameRules.getPossiblePlays(gameBoard, tokenColor);
		if (PossibleMoves.size() == 0) {
			if (GameRules.getPossiblePlays(gameBoard, (byte)-tokenColor).size() == 0) {
				return new AiPlay(difficultyCicle(gameBoard, difficulty), null);
			}
			return max(gameBoard, depth, alpha, beta, (byte)-tokenColor, difficulty);
		}

		byte[][] tmp = new byte[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            System.arraycopy(gameBoard[i], 0, tmp[i], 0, BOARD_SIZE);
		int best = Integer.MAX_VALUE;
		Play play = null;

		for (int i = 0; i < PossibleMoves.size(); i++) {
            beta = Math.min(best, beta);
            if(alpha >= beta){
                break;
            }
			GameRules.plays(gameBoard, PossibleMoves.get(i), tokenColor);
			int value = max(gameBoard, depth - 1, alpha, Math.min(best, beta), (byte)-tokenColor, difficulty).mark;
			if (value < best) {
				best = value;
				play = PossibleMoves.get(i);
			}
            for (int j = 0; j < BOARD_SIZE; j++)
                System.arraycopy(tmp[j], 0, gameBoard[j], 0, BOARD_SIZE);
		}
		return new AiPlay(best, play);
	}

	private static int difficultyCicle(byte[][] gameBoard, int difficulty) {
		int whiteEvaluate = 0;
		int blackEvaluate = 0;
		switch (difficulty) {
			case 1:
				for (int i = 0; i < BOARD_SIZE; i++) {
					for (int j = 0; j < BOARD_SIZE; j++) {
						if (gameBoard[i][j] == WHITE) {
							whiteEvaluate += 1;
						} else if (gameBoard[i][j] == BLACK) {
							blackEvaluate += 1;
						}
					}
				}
				break;
			case 2:
				for (int i = 0; i < BOARD_SIZE; i++) {
					for (int j = 0; j < BOARD_SIZE; j++) {
						if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 5;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 5;
							}
						} else if (i == 0 || i == 7 || j == 0 || j == 7) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 5;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 5;
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
				for (int i = 0; i < BOARD_SIZE; i++) {
					for (int j = 0; j < BOARD_SIZE; j++) {
						if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 10;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 10;
							}
						} else if (i == 0 || i == 7 || j == 0 || j == 7) {
							if (gameBoard[i][j] == WHITE) {
								whiteEvaluate += 10;
							} else if (gameBoard[i][j] == BLACK) {
								blackEvaluate += 10;
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
				blackEvaluate = blackEvaluate * 2 + GameRules.getPossiblePlays(gameBoard, BLACK).size();
				whiteEvaluate = whiteEvaluate * 2 + GameRules.getPossiblePlays(gameBoard, WHITE).size();
				break;
		}
		return blackEvaluate - whiteEvaluate;
	}


}

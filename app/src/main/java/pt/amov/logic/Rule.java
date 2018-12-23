package pt.amov.logic;

import java.util.ArrayList;
import java.util.List;


public class Rule implements Constants{

	public static boolean isPossiblePlay(byte[][] gameBoard, Play play, byte tokenColor) {

		int i, j, dirx, diry, row = play.row, col = play.col;
		if (!isPossible(row, col) || gameBoard[row][col] != Constants.NULL)
			return false;
		for (dirx = -1; dirx < 2; dirx++) {
			for (diry = -1; diry < 2; diry++) {
				if (dirx == 0 && diry == 0)
					continue;
				int x = col + dirx, y = row + diry;
				if (isPossible(y, x) && gameBoard[y][x] == (-tokenColor)) {
					for (i = row + diry * 2, j = col + dirx * 2; isPossible(i, j); i += diry, j += dirx) {
						if (gameBoard[i][j] == (-tokenColor)) {
							continue;
						} else if (gameBoard[i][j] == tokenColor) {
							return true;
						} else {
							break;
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean isPossible(int row, int col) {
		return row >= 0 && row < BOARDSIZE && col >= 0 && col < BOARDSIZE;
	}

	public static List<Play> move(byte[][] gameBoard, Play play, byte tokenColor) {
		int row = play.row;
		int col = play.col;
		int i, j, temp, m, n, dirx, diry;
		List<Play> plays = new ArrayList<>();
		for (dirx = -1; dirx < 2; dirx++) {
			for (diry = -1; diry < 2; diry++) {
				if (dirx == 0 && diry == 0)
					continue;
				temp = 0;
				int x = col + dirx, y = row + diry;
				if (isPossible(y, x) && gameBoard[y][x] == (-tokenColor)) {
					temp++;
					for (i = row + diry * 2, j = col + dirx * 2; isPossible(i, j); i += diry, j += dirx) {
						if (gameBoard[i][j] == (-tokenColor)) {
							temp++;
							continue;
						} else if (gameBoard[i][j] == tokenColor) {
							for (m = row + diry, n = col + dirx; m <= row + temp && m >= row - temp && n <= col + temp
									&& n >= col - temp; m += diry, n += dirx) {
								gameBoard[m][n] = tokenColor;
								plays.add(new Play(m, n));
							}
							break;
						} else
							break;
					}
				}
			}
		}
		gameBoard[row][col] = tokenColor;
		return plays;
	}

	public static List<Play> getPossiblePlays(byte[][] gameBoard, byte tokenColor) {
		List<Play> plays = new ArrayList<>();
		Play play;
		for (int row = 0; row < BOARDSIZE; row++) {
			for (int col = 0; col < BOARDSIZE; col++) {
				play = new Play(row, col);
				if (Rule.isPossiblePlay(gameBoard, play, tokenColor)) {
					plays.add(play);
				}
			}
		}
		return plays;
	}

	public static Scores analyse(byte[][] gameBoard, byte playerColor) {

		int PLAYER = 0;
		int AI = 0;
		for (int i = 0; i < BOARDSIZE; i++) {
			for (int j = 0; j < BOARDSIZE; j++) {
				if (gameBoard[i][j] == playerColor)
					PLAYER += 1;
				else if (gameBoard[i][j] == (byte)-playerColor)
					AI += 1;
			}
		}
		return new Scores(PLAYER, AI);
	}
}

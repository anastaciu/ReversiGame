package pt.amov.reversISEC.logic;

import java.util.ArrayList;
import java.util.List;


public class GameRules implements Constants{

    public static boolean isPossiblePlay(byte[][] gameBoard, Play play, byte tokenColor) {

        int i, j, dirx, diry, row = play.line, col = play.col;
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
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    public static List<Play> plays(byte[][] gameBoard, Play play, byte tokenColor) {
        int row = play.line;
        int col = play.col;
        int temp;
        List<Play> plays = new ArrayList<>();
        for (int x_pos = -1; x_pos < 2; x_pos++) {
            for (int y_pos = -1; y_pos < 2; y_pos++) {
                if (x_pos == 0 && y_pos == 0)
                    continue;
                temp = 0;
                int x = col + x_pos, y = row + y_pos;
                if (isPossible(y, x) && gameBoard[y][x] == (-tokenColor)) {
                    temp++;
                    for (int i = row + y_pos * 2, j = col + x_pos * 2; isPossible(i, j); i += y_pos, j += x_pos) {
                        if (gameBoard[i][j] == (-tokenColor)) {
                            temp++;
                            continue;
                        } else if (gameBoard[i][j] == tokenColor) {
                            for (int m = row + y_pos, n = col + x_pos; m <= row + temp && m >= row - temp && n <= col + temp
                                    && n >= col - temp; m += y_pos, n += x_pos) {
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
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                play = new Play(row, col);
                if (GameRules.isPossiblePlay(gameBoard, play, tokenColor)) {
                    plays.add(play);
                }
            }
        }
        return plays;
    }

    public static Scores getScores(byte[][] gameBoard, byte playerColor) {

        int PLAYER = 0;
        int AI = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (gameBoard[i][j] == playerColor)
                    PLAYER += 1;
                else if (gameBoard[i][j] == (byte)-playerColor)
                    AI += 1;
            }
        }
        return new Scores(PLAYER, AI);
    }
}
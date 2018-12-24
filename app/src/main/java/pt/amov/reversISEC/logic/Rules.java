package pt.amov.reversISEC.logic;

import java.util.ArrayList;
import java.util.List;


public class Rules implements Constants {

    public static boolean isPossiblePlay(byte[][] gameBoard, Play play, byte tokenColor) {

        int dirx, diry, row = play.row, col = play.col;
        if (!isPossible(row, col) || gameBoard[row][col] != Constants.NULL)
            return false;
        for (dirx = -1; dirx < 2; dirx++)
            for (diry = -1; diry < 2; diry++) {
                if (dirx == 0 && diry == 0)
                    continue;
                int x = col + dirx, y = row + diry;
                if (isPossible(y, x) && gameBoard[y][x] == (-tokenColor))
                    for (int i = row + diry * 2, j = col + dirx * 2; isPossible(i, j); i += diry, j += dirx)
                        if (gameBoard[i][j] == tokenColor)
                            return true;
            }
        return false;
    }

    private static boolean isPossible(int row, int col) {
        return row >= 0 && row < BOARDSIZE && col >= 0 && col < BOARDSIZE;
    }

    public static List<Play> plays(byte[][] gameBoard, Play play, byte tokenColor) {

        int row = play.row, col = play.col, temp;
        List<Play> plays = new ArrayList<>();
        for (int dirx = -1; dirx < 2; dirx++) {
            for (int diry = -1; diry < 2; diry++) {
                if (dirx == 0 && diry == 0)
                    continue;
                temp = 0;
                int x = col + dirx, y = row + diry;
                if (isPossible(y, x) && gameBoard[y][x] == (-tokenColor)) {
                    temp++;
                    for (int i = row + diry * 2, j = col + dirx * 2; isPossible(i, j); i += diry, j += dirx) {
                        if (gameBoard[i][j] == (-tokenColor)) {
                            temp++;
                        } else if (gameBoard[i][j] == tokenColor)
                            for (int m = row + diry, n = col + dirx; m <= row + temp && m >= row - temp && n <= col + temp && n >= col - temp; m += diry, n += dirx) {
                                gameBoard[m][n] = tokenColor;
                                plays.add(new Play(m, n));
                            }
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
                if (Rules.isPossiblePlay(gameBoard, play, tokenColor)) {
                    plays.add(play);
                }
            }
        }
        return plays;
    }

    public static Scores getScores(byte[][] gameBoard, byte playerColor) {

        int PLAYER1 = 0;
        int PLAYER2 = 0;
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                if (gameBoard[i][j] == playerColor)
                    PLAYER1 += 1;
                else if (gameBoard[i][j] == (byte) -playerColor)
                    PLAYER2 += 1;
            }
        }
        return new Scores(PLAYER1, PLAYER2);
    }
}

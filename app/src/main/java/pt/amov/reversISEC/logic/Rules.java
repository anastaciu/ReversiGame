package pt.amov.reversISEC.logic;

import java.util.ArrayList;
import java.util.List;


public class Rules implements Constants {

    public static boolean isPossiblePlay(byte[][] gameBoard, Play play, byte tokenColor) {

        int i, j, dirx, diry, row = play.row, col = play.col;
        if (!isLegal(row, col) || gameBoard[row][col] != NULL)
            return false;
        for (dirx = -1; dirx < 2; dirx++) {
            for (diry = -1; diry < 2; diry++) {
                if (dirx == 0 && diry == 0)
                    continue;
                int x = col + dirx, y = row + diry;
                if (isLegal(y, x) && gameBoard[y][x] == (-tokenColor)) {
                    for (i = row + diry * 2, j = col + dirx * 2; isLegal(i, j); i += diry, j += dirx) {
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

    public static boolean isLegal(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public static List<Play> plays(byte[][] chessBoard, Play move, byte chessColor) {
        int row = move.row;
        int col = move.col;
        int i, j, temp, m, n, dirx, diry;
        List<Play> moves = new ArrayList<Play>();
        for (dirx = -1; dirx < 2; dirx++) {
            for (diry = -1; diry < 2; diry++) {
                if (dirx == 0 && diry == 0)
                    continue;
                temp = 0;
                int x = col + dirx, y = row + diry;
                if (isLegal(y, x) && chessBoard[y][x] == (-chessColor)) {
                    temp++;
                    for (i = row + diry * 2, j = col + dirx * 2; isLegal(i, j); i += diry, j += dirx) {
                        if (chessBoard[i][j] == (-chessColor)) {
                            temp++;
                            continue;
                        } else if (chessBoard[i][j] == chessColor) {
                            for (m = row + diry, n = col + dirx; m <= row + temp && m >= row - temp && n <= col + temp
                                    && n >= col - temp; m += diry, n += dirx) {
                                chessBoard[m][n] = chessColor;
                                moves.add(new Play(m, n));
                            }
                            break;
                        } else
                            break;
                    }
                }
            }
        }
        chessBoard[row][col] = chessColor;
        return moves;
    }

    public static List<Play> getPossiblePlays(byte[][] chessBoard, byte chessColor) {
        List<Play> moves = new ArrayList<Play>();
        Play move = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                move = new Play(row, col);
                if (Rules.isPossiblePlay(chessBoard, move, chessColor)) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public static Scores getScores(byte[][] chessBoard, byte playerColor) {

        int PLAYER = 0;
        int AI = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessBoard[i][j] == playerColor)
                    PLAYER += 1;
                else if (chessBoard[i][j] == (byte)-playerColor)
                    AI += 1;
            }
        }
        return new Scores(PLAYER, AI);
    }
}
package pt.amov.reversISEC.logic;

public interface Constants {

    int BOARD_SIZE = 8;
	byte NULL = 0;
	byte WHITE = 1;
	byte BLACK = -1;
	int STATE_PLAYER1_MOVE = 0;
	int STATE_PLAYER2_MOVE = 1;
	int STATE_GAME_OVER = 2;
	String X_TOKENS = " × ";
	int TOKENS = 22;
	String AI_NAME[] = new String[]{"Rookie", "Pro", "Master"};
	int SPECIAL_THRESHOLD = 3;
	String GMODE_1P_STR = "1";
	String GMODE_2P_STR = "2";
	byte GAME_MODE_1P = 1;
	byte GAME_MODE_2P = 2;

}

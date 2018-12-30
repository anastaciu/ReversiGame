package pt.amov.reversISEC.interfaces.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import pt.amov.reversISEC.interfaces.dialog.PlayerInfoDialogBox;
import pt.amov.reversISEC.logic.Play;
import pt.amov.reversISEC.logic.Scores;
import pt.amov.reversISEC.logic.AiAlgorithm;
import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.interfaces.views.GameView;
import pt.amov.reversISEC.logic.GameRules;
import pt.amov.reversISEC.interfaces.dialog.ResultMessage;
import pt.amov.reversISEC.interfaces.dialog.NewGameChooser;
import pt.amov.reversISEC.R;

public class GameVsAiActivity extends Activity implements Constants{


    private GameView gameView = null;
    private LinearLayout player1Layout;
    private LinearLayout player2Layout;
    private TextView player1Tokens;
    private TextView player2Tokens;
    private TextView tvPlayerName;
    private ImageView player1Image;
    private ImageView player2Image;
    private TextView nameOfAI;


    private byte playerColor;
    private byte player2Color;
    private int difficulty;
    private String playerName;



    private static final int depth[] = new int[] { 0, 1, 6, 3, 7, 3, 5, 8, 4 };

    private final byte[][] gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];
    private int gameState;

    private NewGameChooser dialog;
    private PlayerInfoDialogBox playerInfoDialogBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game);
        gameView = findViewById(R.id.gameView);
        tvPlayerName = findViewById(R.id.player1_name);
        player1Layout = findViewById(R.id.player1);
        player2Layout = findViewById(R.id.player2);
        player1Tokens = findViewById(R.id.player1_tokens);
        player2Tokens = findViewById(R.id.player2_tokens);
        player1Image = findViewById(R.id.player1_image);
        player2Image = findViewById(R.id.player2_image);
        nameOfAI = findViewById(R.id.player2_name);
        Button newGame = findViewById(R.id.new_game);
        Button pass = findViewById(R.id.pass);
        Button playAgain = findViewById(R.id.play_again);
        Button quitGame = findViewById(R.id.exit_game);


        Bundle bundle = getIntent().getExtras();
        playerColor = Objects.requireNonNull(bundle).getByte("playerColor");
        player2Color = (byte) -playerColor;
        difficulty = bundle.getInt("difficulty");

        nameOfAI.setText(AI_NAME[difficulty - 1]);

        playerInfoDialogBox = new PlayerInfoDialogBox(GameVsAiActivity.this, tvPlayerName);
        playerInfoDialogBox.show();

        initGameBoard();


        gameView.setOnTouchListener(new OnTouchListener() {

            boolean down = false;
            int downRow;
            int downCol;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gameState != STATE_PLAYER1_MOVE) {
                    return false;
                }
                float x = event.getX();
                float y = event.getY();
                if (gameView.inGameBoard(x, y)) {
                    return false;
                }
                int row = gameView.getLine(y);
                int col = gameView.getCol(x);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down = true;
                        downRow = row;
                        downCol = col;
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        if (down && downRow == row && downCol == col) {
                            down = false;
                            if (!GameRules.isPossiblePlay(gameBoard, new Play(row, col), playerColor)) {
                                return true;
                            }

                            Play play = new Play(row, col);
                            List<Play> plays = GameRules.plays(gameBoard, play, playerColor);
                            gameView.play(gameBoard, plays, play);
                            aiTurn();

                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        down = false;
                        break;
                }
                return true;
            }
        });

        newGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                byte _playerColor = (byte)preferences.getInt("playerColor", BLACK);
                int _difficulty = preferences.getInt("difficulty", 1);

                dialog = new NewGameChooser(GameVsAiActivity.this, _playerColor, _difficulty);

                dialog.setOnStartNewGameListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playerColor = dialog.getPlayerColor();
                        player2Color = (byte) -playerColor;
                        difficulty = dialog.getDifficulty();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        preferences.edit().putInt("playerColor", playerColor).apply();
                        preferences.edit().putInt("difficulty", difficulty).apply();

                        nameOfAI.setText(AI_NAME[difficulty - 1]);

                        initGameBoard();
                        if(playerColor == BLACK){
                            player1Image.setImageResource(R.drawable.black_1);
                            player2Image.setImageResource(R.drawable.white_1);
                            playerTurn();
                        }else{
                            player1Image.setImageResource(R.drawable.white_1);
                            player2Image.setImageResource(R.drawable.black_1);
                            aiTurn();
                        }
                        gameView.initGameBoard();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        quitGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                byte playColor = (byte)preferences.getInt("playerColor", BLACK);
                Intent intent = new Intent(GameVsAiActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putByte("playerColor", playColor);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        if(playerColor == BLACK){
            player1Image.setImageResource(R.drawable.black_1);
            player2Image.setImageResource(R.drawable.white_1);
            playerTurn();
        }else{
            player1Image.setImageResource(R.drawable.white_1);
            player2Image.setImageResource(R.drawable.black_1);
            aiTurn();
        }

    }

    private void initGameBoard(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j] = NULL;
            }
        }
        gameBoard[3][3] = WHITE;
        gameBoard[3][4] = BLACK;
        gameBoard[4][3] = BLACK;
        gameBoard[4][4] = WHITE;
    }


    class AIThread extends Thread {

        private byte AiColor;

        private AIThread(byte AiColor) {
            this.AiColor = AiColor;
        }

        public void run() {
            int process_play = 500;
            try {
                sleep(process_play);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int legalMoves = GameRules.getPossiblePlays(gameBoard, AiColor).size();
            if (legalMoves > 0) {
                Play play = AiAlgorithm.getPlay(gameBoard, depth[difficulty], AiColor, difficulty);
                List<Play> plays = GameRules.plays(gameBoard, play, AiColor);
                gameView.play(gameBoard, plays, play);
            }
            updateUI.handle(legalMoves, AiColor);
        }
    }

    private UpdateUIHandler updateUI = new UpdateUIHandler();
    @SuppressLint("HandlerLeak")
    class UpdateUIHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            int legalMoves = msg.what;
            int thinkingColor = msg.arg1;
            int legalMovesOfAI, legalMovesOfPlayer;
            if(thinkingColor == player2Color){
                legalMovesOfAI = legalMoves;
                legalMovesOfPlayer = GameRules.getPossiblePlays(gameBoard, playerColor).size();

                Scores scores = GameRules.getScores(gameBoard, playerColor);
                if (legalMovesOfAI > 0 && legalMovesOfPlayer > 0) {
                    playerTurn();
                } else if (legalMovesOfAI == 0 && legalMovesOfPlayer > 0) {
                    playerTurn();
                } else if (legalMovesOfAI == 0 && legalMovesOfPlayer == 0) {
                    gameState = STATE_GAME_OVER;
                    gameOver(scores.player1 - scores.player2);
                } else if (legalMovesOfAI > 0 && legalMovesOfPlayer == 0) {
                    aiTurn();
                }
            }else{
                legalMovesOfPlayer = legalMoves;
                legalMovesOfAI = GameRules.getPossiblePlays(gameBoard, player2Color).size();
                Scores scores = GameRules.getScores(gameBoard, playerColor);
                if (legalMovesOfPlayer > 0 && legalMovesOfAI > 0) {
                    aiTurn();
                }else if(legalMovesOfPlayer == 0 && legalMovesOfAI > 0){
                    aiTurn();
                }else if(legalMovesOfPlayer == 0 && legalMovesOfAI == 0){
                    gameState = STATE_GAME_OVER;
                    gameOver(scores.player1 - scores.player2);
                }else if (legalMovesOfPlayer > 0 && legalMovesOfAI == 0) {
                    playerTurn();
                }
            }

        }

        private void handle(int legalMoves, int thinkingColor) {
            removeMessages(0);
            sendMessageDelayed(Message.obtain(updateUI, legalMoves, thinkingColor, 0), 0);
        }
    }

    private void playerTurn(){
        Scores scores = GameRules.getScores(gameBoard, playerColor);
        String playerStats = X_TOKENS + scores.player1;
        String AIStats = X_TOKENS + scores.player2;
        player1Tokens.setText(playerStats);
        player2Tokens.setText( AIStats);
        player1Layout.setBackgroundResource(R.drawable.player_selected);
        player2Layout.setBackgroundResource(R.drawable.player_unselected);
        gameState = STATE_PLAYER1_MOVE;
    }

    private void aiTurn(){
        Scores scores = GameRules.getScores(gameBoard, playerColor);
        String playerStats = X_TOKENS + scores.player1;
        String AIStats = X_TOKENS + scores.player2;
        player1Tokens.setText(playerStats);
        player2Tokens.setText(AIStats);
        player1Layout.setBackgroundResource(R.drawable.player_unselected);
        player2Layout.setBackgroundResource(R.drawable.player_selected);
        gameState = STATE_PLAYER2_MOVE;
        new AIThread(player2Color).start();
    }

    private void gameOver(int gameResult){
        ResultMessage msgDialog;
        String msg;
        if(gameResult > 0){
            msg = "You've beaten " + AI_NAME[difficulty - 1];
        }else if(gameResult == 0){
            msg = "Draw";
        }else{
            msg = "Defeated by " + AI_NAME[difficulty - 1];
        }
        msgDialog = new ResultMessage(GameVsAiActivity.this, msg);
        msgDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(GameVsAiActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            GameVsAiActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

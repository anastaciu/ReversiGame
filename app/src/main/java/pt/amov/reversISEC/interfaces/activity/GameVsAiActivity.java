package pt.amov.reversISEC.interfaces.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import pt.amov.reversISEC.logic.Play;
import pt.amov.reversISEC.logic.Scores;
import pt.amov.reversISEC.logic.AiAlgorithm;
import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.interfaces.views.GameView;
import pt.amov.reversISEC.logic.Rules;
import pt.amov.reversISEC.interfaces.dialog.MessageDialog;
import pt.amov.reversISEC.interfaces.dialog.NewGameDialog;
import pt.amov.reversISEC.R;

public class GameVsAiActivity extends Activity implements Constants{


    private GameView gameView = null;
    private LinearLayout playerLayout;
    private LinearLayout aiLayout;
    private TextView playerTokens;
    private TextView aiTokens;
    private ImageView playerImage;
    private ImageView aiImage;
    private TextView nameOfAI;


    private byte playerColor;
    private byte aiColor;
    private int difficulty;


    private static final int depth[] = new int[] { 0, 1, 6, 3, 7, 3, 5, 8, 4 };

    private final byte[][] gameBoard = new byte[BOARDSIZE][BOARDSIZE];
    private int gameState;

    private static final String MULTIPLY = " × ";
    private static final String NAME_OF_AI[] = new String[]{"Rookie", "Pro", "Master"};

    private NewGameDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game);
        gameView = findViewById(R.id.gameView);
        playerLayout = findViewById(R.id.player);
        aiLayout = findViewById(R.id.ai);
        playerTokens = findViewById(R.id.player_tokens);
        aiTokens = findViewById(R.id.aiChesses);
        playerImage = findViewById(R.id.player_image);
        aiImage = findViewById(R.id.aiImage);
        nameOfAI = findViewById(R.id.name_of_ai);
        Button newGame = findViewById(R.id.new_game);


        Bundle bundle = getIntent().getExtras();
        playerColor = Objects.requireNonNull(bundle).getByte("playerColor");
        aiColor = (byte) -playerColor;
        difficulty = bundle.getInt("difficulty");

        nameOfAI.setText(NAME_OF_AI[difficulty - 1]);

        initGameBoard();


        gameView.setOnTouchListener(new OnTouchListener() {

            boolean down = false;
            int downRow;
            int downCol;



            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gameState != STATE_PLAYER_MOVE) {
                    return false;
                }
                float x = event.getX();
                float y = event.getY();
                if (gameView.inGameBoard(x, y)) {
                    return false;
                }
                int row = gameView.getRow(y);
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
                            if (!Rules.isPossiblePlay(gameBoard, new Play(row, col), playerColor)) {
                                return true;
                            }

                            Play play = new Play(row, col);
                            List<Play> plays = Rules.plays(gameBoard, play, playerColor);
                            gameView.move(gameBoard, plays, play);
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
                byte _playerColor = (byte)preferences.getInt("playerColor", Constants.BLACK);
                int _difficulty = preferences.getInt("difficulty", 1);

                dialog = new NewGameDialog(GameVsAiActivity.this, _playerColor, _difficulty);

                dialog.setOnStartNewGameListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playerColor = dialog.getPlayerColor();
                        aiColor = (byte) -playerColor;
                        difficulty = dialog.getDifficulty();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        preferences.edit().putInt("playerColor", playerColor).apply();
                        preferences.edit().putInt("difficulty", difficulty).apply();

                        nameOfAI.setText(NAME_OF_AI[difficulty - 1]);

                        initGameBoard();
                        if(playerColor == BLACK){
                            playerImage.setImageResource(R.drawable.black1);
                            aiImage.setImageResource(R.drawable.white1);
                            playerTurn();
                        }else{
                            playerImage.setImageResource(R.drawable.white1);
                            aiImage.setImageResource(R.drawable.black1);
                            aiTurn();
                        }
                        gameView.initGameBoard();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        if(playerColor == BLACK){
            playerImage.setImageResource(R.drawable.black1);
            aiImage.setImageResource(R.drawable.white1);
            playerTurn();
        }else{
            playerImage.setImageResource(R.drawable.white1);
            aiImage.setImageResource(R.drawable.black1);
            aiTurn();
        }

    }

    private void initGameBoard(){
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                gameBoard[i][j] = NULL;
            }
        }
        gameBoard[3][3] = WHITE;
        gameBoard[3][4] = BLACK;
        gameBoard[4][3] = BLACK;
        gameBoard[4][4] = WHITE;
    }


    class ThinkingThread extends Thread {

        private byte thinkingColor;

        private ThinkingThread(byte thinkingColor) {
            this.thinkingColor = thinkingColor;
        }

        public void run() {
            int process_move = 500;
            try {
                sleep(process_move);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int legalMoves = Rules.getPossiblePlays(gameBoard, thinkingColor).size();
            if (legalMoves > 0) {
                Play play = AiAlgorithm.getPlay(gameBoard, depth[difficulty], thinkingColor, difficulty);
                List<Play> plays = Rules.plays(gameBoard, play, thinkingColor);
                gameView.move(gameBoard, plays, play);
            }
            updateUI.handle(legalMoves, thinkingColor);
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
            if(thinkingColor == aiColor){
                legalMovesOfAI = legalMoves;
                legalMovesOfPlayer = Rules.getPossiblePlays(gameBoard, playerColor).size();

                Scores scores = Rules.getScores(gameBoard, playerColor);
                if (legalMovesOfAI > 0 && legalMovesOfPlayer > 0) {
                    playerTurn();
                } else if (legalMovesOfAI == 0 && legalMovesOfPlayer > 0) {
                    playerTurn();
                } else if (legalMovesOfAI == 0 && legalMovesOfPlayer == 0) {
                    gameState = STATE_GAME_OVER;
                    gameOver(scores.PLAYER1 - scores.PLAYER2);
                } else if (legalMovesOfAI > 0 && legalMovesOfPlayer == 0) {
                    aiTurn();
                }
            }else{
                legalMovesOfPlayer = legalMoves;
                legalMovesOfAI = Rules.getPossiblePlays(gameBoard, aiColor).size();
                Scores scores = Rules.getScores(gameBoard, playerColor);
                if (legalMovesOfPlayer > 0 && legalMovesOfAI > 0) {
                    aiTurn();
                }else if(legalMovesOfPlayer == 0 && legalMovesOfAI > 0){
                    aiTurn();
                }else if(legalMovesOfPlayer == 0 && legalMovesOfAI == 0){
                    gameState = STATE_GAME_OVER;
                    gameOver(scores.PLAYER1 - scores.PLAYER2);
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
        Scores scores = Rules.getScores(gameBoard, playerColor);
        String playerStats = MULTIPLY + scores.PLAYER1;
        String AIStats = MULTIPLY + scores.PLAYER2;
        playerTokens.setText(playerStats);
        aiTokens.setText( AIStats);
        playerLayout.setBackgroundResource(R.drawable.rect);
        aiLayout.setBackgroundResource(R.drawable.rect_normal);
        gameState = STATE_PLAYER_MOVE;
    }

    private void aiTurn(){
        Scores scores = Rules.getScores(gameBoard, playerColor);
        String playerStats = MULTIPLY + scores.PLAYER1;
        String AIStats = MULTIPLY + scores.PLAYER2;
        playerTokens.setText(playerStats);
        aiTokens.setText(AIStats);
        playerLayout.setBackgroundResource(R.drawable.rect_normal);
        aiLayout.setBackgroundResource(R.drawable.rect);
        gameState = STATE_AI_MOVE;
        new ThinkingThread(aiColor).start();
    }

    private void gameOver(int gameResult){
        MessageDialog msgDialog;
        String msg;
        if(gameResult > 0){
            msg = "You've beaten " + NAME_OF_AI[difficulty - 1];
        }else if(gameResult == 0){
            msg = "Draw";
        }else{
            msg = "Defeated by " + NAME_OF_AI[difficulty - 1];
        }
        msgDialog = new MessageDialog(GameVsAiActivity.this, msg);
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

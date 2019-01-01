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
import android.support.v4.content.ContextCompat;
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
    private ImageView player1Image;
    private ImageView player2Image;
    private TextView nameOfAI;
    private boolean player1_turn = true;

    private byte playerColor;
    private byte player2Color;
    private int difficulty;

    private int player1TurnSpecialMoves = 0;
    private boolean player1PassNotUsed = true;
    private boolean player1TwiceSpecialPlayActive = false;
    private boolean player1TwiceNotUsed = true;
    private boolean player1PlayTwiceInUse = false;



    private static final int depth[] = new int[] { 0, 2, 3, 4};

    private final byte[][] gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];
    private int gameState;

    private NewGameChooser dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game);
        gameView = findViewById(R.id.gameView);
        player1Layout = findViewById(R.id.player1);
        player2Layout = findViewById(R.id.player2);
        player1Tokens = findViewById(R.id.player1_tokens);
        player2Tokens = findViewById(R.id.player2_tokens);
        player1Image = findViewById(R.id.player1_image);
        player2Image = findViewById(R.id.player2_image);
        nameOfAI = findViewById(R.id.player2_name);
        TextView tvPlayerName = findViewById(R.id.player1_name);
        int newGameAI;

        final Button newGame = findViewById(R.id.new_game);
        final Button pass = findViewById(R.id.pass);
        final Button playAgain = findViewById(R.id.play_again);
        final Button quitGame = findViewById(R.id.exit_game);

        PlayerInfoDialogBox playerInfoDialogBox;
        setButtonOff(playAgain);
        setButtonOff(pass);

        Bundle bundle = getIntent().getExtras();
        playerColor = Objects.requireNonNull(bundle).getByte("playerColor");
        player2Color = (byte) -playerColor;
        difficulty = bundle.getInt("difficulty");
        newGameAI = bundle.getInt("newGameAI");

        nameOfAI.setText(AI_NAME[difficulty - 1]);

        if(newGameAI == 0){
            playerInfoDialogBox = new PlayerInfoDialogBox(GameVsAiActivity.this, tvPlayerName);
            playerInfoDialogBox.show();
        }

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
                            if(!player1PlayTwiceInUse)
                                aiTurn();
                            player1TurnSpecialMoves++;
                        }

                        if (!player1_turn && !player1TwiceNotUsed && player1TwiceSpecialPlayActive) {
                            setPlayerColor(player1Layout, player2Layout);
                            player1TwiceSpecialPlayActive = false;
                            setButtonOff(playAgain);
                            setButtonOff(pass);
                        }


                        if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD+1 && player1PassNotUsed) {
                            setButtonOn(pass);
                        } else {
                            setButtonOff(pass);
                        }

                        if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD+1 && player1TwiceNotUsed) {
                            setButtonOn(playAgain);
                        }  else {
                            setButtonOff(playAgain);
                        }

                        if(player1PlayTwiceInUse) {
                            setButtonOff(pass);
                            player1PlayTwiceInUse = !player1PlayTwiceInUse;
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
                        GameVsAiActivity.this.finish();



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
                        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        byte playColor = (byte) preferences.getInt("playerColor", -1);
                        int difficulty = preferences.getInt("difficulty", 1);
                        Intent intent = new Intent(GameVsAiActivity.this, GameVsAiActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putByte("playerColor", playColor);
                        bundle.putInt("difficulty", difficulty);
                        intent.putExtras(bundle);
                        intent.putExtra("newGameAI", 1);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        gameView.initGameBoard();
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        pass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aiTurn();
                player1PassNotUsed = false;
                setButtonOff(pass);

            }


        });

        playAgain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                player1TwiceNotUsed = false;
                player1PlayTwiceInUse = true;
                setButtonOff(playAgain);
                setButtonOff(pass);
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
                finish();
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
            int Ai_Color = msg.arg1;
            int legalMovesOfAI, legalMovesOfPlayer;
            if(Ai_Color == player2Color){
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
        if(player1PlayTwiceInUse){
            playerTurn();
            player1PlayTwiceInUse = false;
        }
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


    void setPlayerColor(LinearLayout player1Layout, LinearLayout player2Layout) {
        player1Layout.setBackgroundResource(R.drawable.player_selected);
        player2Layout.setBackgroundResource(R.drawable.player_unselected);
    }

    void setButtonOn(Button button) {
        button.setEnabled(true);
        button.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.design_default_color_primary_dark));
        button.setBackgroundResource(R.drawable.button_bg);
    }

    void setButtonOff(Button button) {
        button.setEnabled(false);
        button.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.WHITE));
        button.setBackgroundResource(R.drawable.button_disabled);
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
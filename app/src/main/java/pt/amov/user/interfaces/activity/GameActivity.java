package pt.amov.user.interfaces.activity;

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

import pt.amov.bean.Move;
import pt.amov.bean.Statistic;
import pt.amov.game.Algorithm;
import pt.amov.game.Constants;
import pt.amov.user.interfaces.views.ReversiView;
import pt.amov.game.Rule;
import pt.amov.user.interfaces.dialog.MessageDialog;
import pt.amov.user.interfaces.dialog.NewGameDialog;
import pt.amov.reversi.R;

public class GameActivity extends Activity {

    private static final byte NULL = Constants.NULL;
    private static final byte BLACK = Constants.BLACK;
    private static final byte WHITE = Constants.WHITE;

    private static final int STATE_PLAYER_MOVE = 0;
    private static final int STATE_AI_MOVE = 1;
    private static final int STATE_GAME_OVER = 2;

    private ReversiView reversiView = null;
    private LinearLayout playerLayout;
    private LinearLayout aiLayout;
    private TextView playerChesses;
    private TextView aiChesses;
    private ImageView playerImage;
    private ImageView aiImage;
    private TextView nameOfAI;


    private byte playerColor;
    private byte aiColor;
    private int difficulty;


    private static final int M = 8;
    private static final int depth[] = new int[] { 0, 1, 2, 3, 7, 3, 5, 2, 4 };

    private final byte[][] chessBoard = new byte[M][M];
    private int gameState;

    private static final String MULTIPLY = " × ";
    private static final String NAME_OF_AI[] = new String[]{"Starter", "Rookie", "Good", "Pro", "Great", "Champ", "Master", "Legend"};

    private NewGameDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game);
        reversiView = findViewById(R.id.reversiView);
        playerLayout = findViewById(R.id.player);
        aiLayout = findViewById(R.id.ai);
        playerChesses = findViewById(R.id.player_chesses);
        aiChesses = findViewById(R.id.aiChesses);
        playerImage = findViewById(R.id.player_image);
        aiImage = findViewById(R.id.aiImage);
        nameOfAI = findViewById(R.id.name_of_ai);
        Button newGame = findViewById(R.id.new_game);
        Button tip = findViewById(R.id.tip);


        Bundle bundle = getIntent().getExtras();
        playerColor = Objects.requireNonNull(bundle).getByte("playerColor");
        aiColor = (byte) -playerColor;
        difficulty = bundle.getInt("difficulty");

        nameOfAI.setText(NAME_OF_AI[difficulty - 1]);

        initialChessboard();


        reversiView.setOnTouchListener(new OnTouchListener() {

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
                if (reversiView.inChessBoard(x, y)) {
                    return false;
                }
                int row = reversiView.getRow(y);
                int col = reversiView.getCol(x);
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
                            if (!Rule.isLegalMove(chessBoard, new Move(row, col), playerColor)) {
                                return true;
                            }

                            Move move = new Move(row, col);
                            List<Move> moves = Rule.move(chessBoard, move, playerColor);
                            reversiView.move(chessBoard, moves, move);
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

                dialog = new NewGameDialog(GameActivity.this, _playerColor, _difficulty);

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

                        initialChessboard();
                        if(playerColor == BLACK){
                            playerImage.setImageResource(R.drawable.black1);
                            aiImage.setImageResource(R.drawable.white1);
                            playerTurn();
                        }else{
                            playerImage.setImageResource(R.drawable.white1);
                            aiImage.setImageResource(R.drawable.black1);
                            aiTurn();
                        }
                        reversiView.initialChessBoard();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameState != STATE_PLAYER_MOVE){
                    return;
                }
                new ThinkingThread(playerColor).start();
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

    private void initialChessboard(){
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                chessBoard[i][j] = NULL;
            }
        }
        chessBoard[3][3] = WHITE;
        chessBoard[3][4] = BLACK;
        chessBoard[4][3] = BLACK;
        chessBoard[4][4] = WHITE;
    }


    class ThinkingThread extends Thread {

        private byte thinkingColor;

        private ThinkingThread(byte thinkingColor) {
            this.thinkingColor = thinkingColor;
        }

        public void run() {
            try {
                sleep(20 * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int legalMoves = Rule.getLegalMoves(chessBoard, thinkingColor).size();
            if (legalMoves > 0) {
                Move move = Algorithm.getGoodMove(chessBoard, depth[difficulty], thinkingColor, difficulty);
                List<Move> moves = Rule.move(chessBoard, move, thinkingColor);
                reversiView.move(chessBoard, moves, move);
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
                legalMovesOfPlayer = Rule.getLegalMoves(chessBoard, playerColor).size();

                Statistic statistic = Rule.analyse(chessBoard, playerColor);
                if (legalMovesOfAI > 0 && legalMovesOfPlayer > 0) {
                    playerTurn();
                } else if (legalMovesOfAI == 0 && legalMovesOfPlayer > 0) {
                    playerTurn();
                } else if (legalMovesOfAI == 0 && legalMovesOfPlayer == 0) {
                    gameState = STATE_GAME_OVER;
                    gameOver(statistic.PLAYER - statistic.AI);
                } else if (legalMovesOfAI > 0 && legalMovesOfPlayer == 0) {
                    aiTurn();
                }
            }else{
                legalMovesOfPlayer = legalMoves;
                legalMovesOfAI = Rule.getLegalMoves(chessBoard, aiColor).size();
                Statistic statistic = Rule.analyse(chessBoard, playerColor);
                if (legalMovesOfPlayer > 0 && legalMovesOfAI > 0) {
                    aiTurn();
                }else if(legalMovesOfPlayer == 0 && legalMovesOfAI > 0){
                    aiTurn();
                }else if(legalMovesOfPlayer == 0 && legalMovesOfAI == 0){
                    gameState = STATE_GAME_OVER;
                    gameOver(statistic.PLAYER - statistic.AI);
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
        Statistic statistic = Rule.analyse(chessBoard, playerColor);
        String playerStats = MULTIPLY + statistic.PLAYER;
        String AIStats = MULTIPLY + statistic.AI;
        playerChesses.setText(playerStats);
        aiChesses.setText( AIStats);
        playerLayout.setBackgroundResource(R.drawable.rect);
        aiLayout.setBackgroundResource(R.drawable.rect_normal);
        gameState = STATE_PLAYER_MOVE;
    }

    private void aiTurn(){
        Statistic statistic = Rule.analyse(chessBoard, playerColor);
        String playerStats = MULTIPLY + statistic.PLAYER;
        String AIStats = MULTIPLY + statistic.AI;
        playerChesses.setText(playerStats);
        aiChesses.setText(AIStats);
        playerLayout.setBackgroundResource(R.drawable.rect_normal);
        aiLayout.setBackgroundResource(R.drawable.rect);
        gameState = STATE_AI_MOVE;
        new ThinkingThread(aiColor).start();
    }

    private void gameOver(int winOrLoseOrDraw){
        MessageDialog msgDialog;
        String msg;
        if(winOrLoseOrDraw > 0){
            msg = "You've beaten " + NAME_OF_AI[difficulty - 1];
        }else if(winOrLoseOrDraw == 0){
            msg = "Draw";
        }else{
            msg = "Defeated by " + NAME_OF_AI[difficulty - 1];
        }
        msgDialog = new MessageDialog(GameActivity.this, msg);
        msgDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            GameActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

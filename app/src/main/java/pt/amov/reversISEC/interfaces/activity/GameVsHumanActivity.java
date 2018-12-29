package pt.amov.reversISEC.interfaces.activity;

import android.app.Activity;
import android.os.Bundle;
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

import pt.amov.reversISEC.R;
import pt.amov.reversISEC.interfaces.dialog.ResultMessage;
import pt.amov.reversISEC.interfaces.views.GameView;
import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.logic.Play;
import pt.amov.reversISEC.logic.GameRules;
import pt.amov.reversISEC.logic.Scores;

public class GameVsHumanActivity extends Activity implements Constants{

    private GameView gameView = null;
    private LinearLayout player1Layout;
    private LinearLayout player2Layout;
    private TextView player1Tokens;
    private TextView player2Tokens;
    private ImageView player1Image;
    private ImageView player2Image;
    private TextView player1Name;
    private TextView player2Name;


    private byte player1Color;
    private byte player2Color;
    private boolean player_vs = false;


    private final byte[][] gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];


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
        Button newGame = findViewById(R.id.new_game);
        Button pass = findViewById(R.id.pass);
        Button playAgain = findViewById(R.id.play_again);
        Button quitGame = findViewById(R.id.exit_game);


        player1Name = findViewById(R.id.player1_name);
        player1Name.setText("The Man");
        player2Name = findViewById(R.id.player2_name);
        player2Name.setText("The Rat");


        Bundle bundle = getIntent().getExtras();
        player1Color = Objects.requireNonNull(bundle).getByte("playerColor");
        player2Color = (byte) -player1Color;

        initGameBoard();
        playerTurn(player1Color, player1Layout, player2Layout);

        gameView.setOnTouchListener(new OnTouchListener() {

            boolean down = false;
            int downRow;
            int downCol;


            @Override
            public boolean onTouch(View v, MotionEvent event) {

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

                            if(player_vs) {
                                if (!GameRules.isPossiblePlay(gameBoard, new Play(row, col), player1Color)) {
                                    return true;
                                }

                                Play play = new Play(row, col);
                                List<Play> plays = GameRules.plays(gameBoard, play, player1Color);
                                gameView.play(gameBoard, plays, play);
                                playerTurn(player1Color, player2Layout, player1Layout);
                            }
                            else {
                                if (!GameRules.isPossiblePlay(gameBoard, new Play(row, col), player2Color)) {
                                    return true;
                                }

                                Play play2 = new Play(row, col);
                                List<Play> plays2 = GameRules.plays(gameBoard, play2, player2Color);
                                gameView.play(gameBoard, plays2, play2);
                                playerTurn(player1Color, player1Layout, player2Layout);
                            }
                        }
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


                        initGameBoard();

                        gameView.initGameBoard();


            }
        });


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


  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(GameVsHumanActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            GameVsHumanActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/


    private void gameOver(int gameResult){
        ResultMessage msgDialog;
        String msg;
        if(gameResult > 0){
            msg = player1Name + " wins";
        }else if(gameResult == 0){
            msg = "Draw";
        }else{
            msg = player2Name + " wins";
        }
        msgDialog = new ResultMessage(GameVsHumanActivity.this, msg);
        msgDialog.show();
    }

    private void playerTurn(byte playerColor, LinearLayout player1Layout, LinearLayout player2Layout){
        Scores scores = GameRules.getScores(gameBoard, playerColor);
        String player1Stats = X_TOKENS + scores.player1;
        String player2Stats = X_TOKENS + scores.player2;
        player1Tokens.setText(player1Stats);
        player2Tokens.setText(player2Stats);
        player1Layout.setBackgroundResource(R.drawable.player_selected);
        player2Layout.setBackgroundResource(R.drawable.player_unselected);
        player_vs = !player_vs;
    }



}

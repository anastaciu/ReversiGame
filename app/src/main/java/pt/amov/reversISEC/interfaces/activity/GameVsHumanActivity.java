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
    private boolean player1_turn = true;
    private int player1TurnSpecial = 0;
    private int player2TurnSpecial = 0;
    private  boolean player1PassNotUsed = true;
    private  boolean player2PassNotUsed = true;
    private int player1TwiceSpecial = 0;
    private int player2TwiceSpecial = 0;
    private boolean player1TwiceNotUsed = true;
    private boolean player2TwiceNotUsed = true;
    private int playsPlayer1 = 2;
    private int playsPlayer2 = 2;


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

        final Button newGame = findViewById(R.id.new_game);
        final Button pass = findViewById(R.id.pass);
        final Button playAgain = findViewById(R.id.play_again);
        final Button quitGame = findViewById(R.id.exit_game);


        playAgain.setEnabled(false);
        playAgain.setVisibility(View.INVISIBLE);

        pass.setEnabled(false);
        pass.setVisibility(View.INVISIBLE);


        player1Name = findViewById(R.id.player1_name);
        player1Name.setText("Ricardo");
        player2Name = findViewById(R.id.player2_name);
        player2Name.setText("Tonixa");

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

                Scores scores;
                float x = event.getX();
                float y = event.getY();
                if (gameView.inGameBoard(x, y)) {
                    return false;
                }
                int line = gameView.getLine(y);
                int col = gameView.getCol(x);


                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        down = true;
                        downRow = line;
                        downCol = col;
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        if (down && downRow == line && downCol == col) {
                            down = false;



                            if(player1_turn) {
                                if (!GameRules.isPossiblePlay(gameBoard, new Play(line, col), player1Color)) {
                                    return true;
                                }

                                Play play = new Play(line, col);
                                List<Play> plays = GameRules.plays(gameBoard, play, player1Color);
                                gameView.play(gameBoard, plays, play);
                                scores = playerTurn(player1Color, player2Layout, player1Layout);
                                player1TurnSpecial++;
                                player1_turn = false;
                            }
                            else {
                                if (!GameRules.isPossiblePlay(gameBoard, new Play(line, col), player2Color)) {
                                    return true;
                                }

                                Play play2 = new Play(line, col);
                                List<Play> plays2 = GameRules.plays(gameBoard, play2, player2Color);
                                gameView.play(gameBoard, plays2, play2);
                                scores = playerTurn(player1Color, player1Layout, player2Layout);
                                player2TurnSpecial++;
                                player1_turn = true;
                            }
                            int legalMovesPlayer1 = GameRules.getPossiblePlays(gameBoard,player1Color).size();
                            int legalMovesPlayer2 = GameRules.getPossiblePlays(gameBoard,player2Color).size();
                            if(legalMovesPlayer2== 0 && legalMovesPlayer1 == 0)
                                gameOver(scores.player1 - scores.player2);
                            else if(legalMovesPlayer1 > 0 && legalMovesPlayer2 == 0){
                                player1Layout.setBackgroundResource(R.drawable.player_selected);
                                player2Layout.setBackgroundResource(R.drawable.player_unselected);
                                player1_turn = true;
                            }
                            else if(legalMovesPlayer2 > 0 && legalMovesPlayer1 == 0){
                                player2Layout.setBackgroundResource(R.drawable.player_selected);
                                player1Layout.setBackgroundResource(R.drawable.player_unselected);
                                player1_turn = false;
                            }

                            if(player1_turn && player1TurnSpecial > SPECIAL && player1PassNotUsed){
                                pass.setEnabled(true);
                                pass.setVisibility(View.VISIBLE);
                            }
                            else if(!player1_turn && player2TurnSpecial > SPECIAL && player2PassNotUsed) {
                                pass.setEnabled(true);
                                pass.setVisibility(View.VISIBLE);
                            }
                            else{
                                pass.setEnabled(false);
                                pass.setVisibility(View.INVISIBLE);
                            }

                            if(player1_turn && player1TurnSpecial > SPECIAL && player1TwiceNotUsed){
                                playAgain.setEnabled(true);
                                playAgain.setVisibility(View.VISIBLE);
                            }
                            else if(!player1_turn && player2TurnSpecial > SPECIAL && player2TwiceNotUsed) {
                                playAgain.setEnabled(true);
                                playAgain.setVisibility(View.VISIBLE);
                            }
                            else{
                                playAgain.setEnabled(false);
                                playAgain.setVisibility(View.INVISIBLE);
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
        pass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (player1_turn) {
                    player1_turn = false;
                    player2Layout.setBackgroundResource(R.drawable.player_selected);
                    player1Layout.setBackgroundResource(R.drawable.player_unselected);
                    player1PassNotUsed = false;
                    player1TurnSpecial++;

                }
                else {
                    player1_turn = true;
                    player1Layout.setBackgroundResource(R.drawable.player_selected);
                    player2Layout.setBackgroundResource(R.drawable.player_unselected);
                    player2PassNotUsed = false;
                    player2TurnSpecial++;
                }

                if(player1_turn && player1TurnSpecial > SPECIAL && player1PassNotUsed){
                    pass.setEnabled(true);
                    pass.setVisibility(View.VISIBLE);
                }
                else if(!player1_turn && player2TurnSpecial > SPECIAL && player2PassNotUsed) {
                    pass.setEnabled(true);
                    pass.setVisibility(View.VISIBLE);
                }
                else{
                    pass.setEnabled(false);
                    pass.setVisibility(View.INVISIBLE);
                }

            }
        });

        playAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                player1TwiceNotUsed = false;
                player2TwiceNotUsed = false;


                if(player1_turn && player1TurnSpecial > SPECIAL && player1TwiceNotUsed){
                    playAgain.setEnabled(true);
                    playAgain.setVisibility(View.VISIBLE);
                }
                else if(!player1_turn && player2TurnSpecial > SPECIAL && player2TwiceNotUsed) {
                    playAgain.setEnabled(true);
                    playAgain.setVisibility(View.VISIBLE);
                }
                else{
                    playAgain.setEnabled(false);
                    playAgain.setVisibility(View.INVISIBLE);
                }

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


    private void gameOver(int gameResult){
        ResultMessage msgDialog;
        String msg;
        if(gameResult > 0){
            msg = player1Name.getText() + " wins";
        }else if(gameResult == 0){
            msg = "Draw";
        }else{
            msg = player2Name.getText() + " wins";
        }
        msgDialog = new ResultMessage(GameVsHumanActivity.this, msg);
        msgDialog.show();
    }

    private Scores playerTurn(byte playerColor, LinearLayout player1Layout, LinearLayout player2Layout){
        Scores scores = GameRules.getScores(gameBoard, playerColor);
        String player1Stats = X_TOKENS + scores.player1;
        String player2Stats = X_TOKENS + scores.player2;
        player1Tokens.setText(player1Stats);
        player2Tokens.setText(player2Stats);
        player1Layout.setBackgroundResource(R.drawable.player_selected);
        player2Layout.setBackgroundResource(R.drawable.player_unselected);
        return scores;
    }



}

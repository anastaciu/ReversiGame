package pt.amov.reversISEC.interfaces.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;



import java.util.List;
import java.util.Objects;

import pt.amov.reversISEC.R;
import pt.amov.reversISEC.interfaces.dialog.TwoPlayerInfoDialogBox;
import pt.amov.reversISEC.interfaces.dialog.ResultMessage;
import pt.amov.reversISEC.interfaces.views.GameView;
import pt.amov.reversISEC.logic.AiAlgorithm;
import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.logic.Play;
import pt.amov.reversISEC.logic.GameRules;
import pt.amov.reversISEC.logic.Scores;



public class GameVsHumanActivity extends Activity implements Constants {

    private GameView gameView = null;
    private LinearLayout player1Layout;
    private LinearLayout player2Layout;
    private TextView player1Tokens;
    private TextView player2Tokens;
    private TextView player1Name;
    private TextView player2Name;

    private boolean isNameP1Defined = false;
    private boolean isNameP2Defined = false;

    private byte player1Color;
    private byte player2Color;
    private boolean player1_turn = true;
    private int player1TurnSpecialMoves = 0;
    private int player2TurnSpecialMoves = 0;
    private boolean player1PassNotUsed = true;
    private boolean player2PassNotUsed = true;
    private boolean player1TwiceSpecialPlayActive = false;
    private boolean player2TwiceSpecialPlayActive = false;
    private boolean player1TwiceNotUsed = true;
    private boolean player2TwiceNotUsed = true;
    private boolean player1PlayTwiceInUse = false;
    private boolean player2PlayTwiceInUse = false;
    private int gameMode = 2;


    private static final int depth[] = new int[] { 0, 2, 3, 4};
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
        player1Name = findViewById(R.id.player1_name);
        player2Name = findViewById(R.id.player2_name);

        final TwoPlayerInfoDialogBox twoPlayerInfoDialogBox;

        final Button newGame = findViewById(R.id.new_game);
        final Button pass = findViewById(R.id.pass);
        final Button playAgain = findViewById(R.id.play_again);
        final Button quitGame = findViewById(R.id.exit_game);

        quitGame.setText(R.string._1_jogador);

        setButtonOff(playAgain);
        setButtonOff(pass);




        Bundle bundle = getIntent().getExtras();
        player1Color = Objects.requireNonNull(bundle).getByte("playerColor");
        player2Color = (byte) -player1Color;
        player1Name.setText(Objects.requireNonNull(bundle).getString("player1Name"));
        player2Name.setText(Objects.requireNonNull(bundle).getString("player2Name"));
        isNameP1Defined = Objects.requireNonNull(bundle).getBoolean("isNamePlayer1Defined");
        isNameP2Defined = Objects.requireNonNull(bundle).getBoolean("isNamePlayer2Defined");





        if(!isNameP1Defined) {
            twoPlayerInfoDialogBox = new TwoPlayerInfoDialogBox(GameVsHumanActivity.this, player1Name, player2Name);
            twoPlayerInfoDialogBox.show();
            isNameP1Defined = !isNameP1Defined;
            isNameP2Defined = !isNameP2Defined;
        }

        initGameBoard();

        playerTurn(player1Color, player1Layout, player2Layout);
        gameView.setOnTouchListener(new OnTouchListener() {

            boolean down = false;
            int downLine;
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
                        downLine = line;
                        downCol = col;
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        if (down && downLine == line && downCol == col) {
                            down = false;



                            if (player1_turn) {
                                if (!GameRules.isPossiblePlay(gameBoard, new Play(line, col), player1Color)) {
                                    return true;
                                }


                                Play play = new Play(line, col);
                                List<Play> plays = GameRules.plays(gameBoard, play, player1Color);
                                gameView.play(gameBoard, plays, play);
                                scores = playerTurn(player1Color, player2Layout, player1Layout);
                                player1TurnSpecialMoves++;
                                player1_turn = false;
                            } else {
                                if (!GameRules.isPossiblePlay(gameBoard, new Play(line, col), player2Color)) {
                                    return true;
                                }

                                Play play2 = new Play(line, col);
                                List<Play> plays2 = GameRules.plays(gameBoard, play2, player2Color);
                                gameView.play(gameBoard, plays2, play2);
                                scores = playerTurn(player1Color, player1Layout, player2Layout);
                                player2TurnSpecialMoves++;
                                player1_turn = true;
                            }
                            int legalMovesPlayer1 = GameRules.getPossiblePlays(gameBoard, player1Color).size();
                            int legalMovesPlayer2 = GameRules.getPossiblePlays(gameBoard, player2Color).size();

                            if (legalMovesPlayer2 == 0 && legalMovesPlayer1 == 0)
                                gameOver(scores.player1 - scores.player2, pass, playAgain);
                            else if (legalMovesPlayer1 > 0 && legalMovesPlayer2 == 0) {
                                setPlayerColor(player1Layout, player2Layout);
                                player1_turn = true;
                            } else if (legalMovesPlayer2 > 0 && legalMovesPlayer1 == 0) {
                                setPlayerColor(player2Layout, player1Layout);
                                player1_turn = false;
                            }

                            if (!player1_turn && !player1TwiceNotUsed && player1TwiceSpecialPlayActive) {
                                setPlayerColor(player1Layout, player2Layout);
                                player1TwiceSpecialPlayActive = false;
                                setButtonOff(playAgain);
                                setButtonOff(pass);
                                player2PlayTwiceInUse = true;
                                player1_turn = !player1_turn;
                            }

                            if (player1_turn && !player2TwiceNotUsed && player2TwiceSpecialPlayActive) {
                                setPlayerColor(player2Layout, player1Layout);
                                player2TwiceSpecialPlayActive = false;
                                setButtonOff(playAgain);
                                setButtonOff(pass);
                                player1PlayTwiceInUse = true;
                                player1_turn = !player1_turn;

                            }

                            if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD && player1PassNotUsed) {
                                setButtonOn(pass);
                            } else if (!player1_turn && player2TurnSpecialMoves > SPECIAL_THRESHOLD && player2PassNotUsed) {
                                setButtonOn(pass);
                            } else {
                                setButtonOff(pass);
                            }

                            if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD && player1TwiceNotUsed) {
                                setButtonOn(playAgain);
                            } else if (!player1_turn && player2TurnSpecialMoves > SPECIAL_THRESHOLD && player2TwiceNotUsed) {
                                setButtonOn(playAgain);
                            } else {
                                setButtonOff(playAgain);
                            }

                            if(player2PlayTwiceInUse) {
                                setButtonOff(pass);
                                player2PlayTwiceInUse = !player2PlayTwiceInUse;
                            }

                            if(player1PlayTwiceInUse) {
                                setButtonOff(pass);
                                player1PlayTwiceInUse = !player1PlayTwiceInUse;
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                byte playColor = (byte) preferences.getInt("playerColor", BLACK);
                Intent intent = new Intent(GameVsHumanActivity.this, GameVsHumanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putByte("playerColor", playColor);
                intent.putExtras(bundle);
                intent.putExtra("isNamePlayer1Defined", isNameP1Defined);
                intent.putExtra("isNamePlayer1Defined", isNameP1Defined);
                intent.putExtra("player1Name",player1Name.getText().toString());
                intent.putExtra("player2Name", player2Name.getText().toString());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            }
        });

        pass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (player1_turn) {
                    player1_turn = false;
                    setPlayerColor(player2Layout, player1Layout);
                    player1PassNotUsed = false;

                } else {
                    player1_turn = true;
                    setPlayerColor(player1Layout, player2Layout);
                    player2PassNotUsed = false;
                }

                if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD && player1PassNotUsed) {
                    setButtonOn(pass);
                } else if (!player1_turn && player2TurnSpecialMoves > SPECIAL_THRESHOLD && player2PassNotUsed) {
                    setButtonOn(pass);
                } else {
                    setButtonOff(pass);
                }
                if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD && player1TwiceNotUsed) {
                    setButtonOn(playAgain);
                } else if (!player1_turn && player2TurnSpecialMoves > SPECIAL_THRESHOLD && player2TwiceNotUsed) {
                    setButtonOn(playAgain);
                } else {
                    setButtonOff(playAgain);
                }
            }
        });

        playAgain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD && player1PassNotUsed) {
                    setButtonOn(pass);
                } else if (!player1_turn && player2TurnSpecialMoves > SPECIAL_THRESHOLD && player2PassNotUsed) {
                    setButtonOn(pass);
                } else {
                    setButtonOff(pass);
                }

                if (player1_turn && player1TurnSpecialMoves > SPECIAL_THRESHOLD && player1TwiceNotUsed) {
                    setButtonOn(playAgain);
                } else if (!player1_turn && player2TurnSpecialMoves > SPECIAL_THRESHOLD && player2TwiceNotUsed) {
                    setButtonOn(playAgain);
                } else {
                    setButtonOff(playAgain);
                }

                if (player1_turn) {
                    player1TwiceNotUsed = false;
                    player1TwiceSpecialPlayActive = true;
                    setButtonOff(playAgain);
                    setButtonOff(pass);
                } else {
                    player2TwiceNotUsed = false;
                    player2TwiceSpecialPlayActive = true;
                    setButtonOff(playAgain);
                    setButtonOff(pass);
                }


            }
        });

        quitGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameMode == GAME_MODE_2P){
                    quitGame.setText(R.string._1_jogador);
                    gameMode = GAME_MODE_1P;

                }
                else if(gameMode == GAME_MODE_1P){
                    quitGame.setText(R.string._2_jogadores);
                    gameMode = GAME_MODE_2P;

                }
            }
        });
    }


    private void initGameBoard() {
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


    private void gameOver(int gameResult, Button pass, Button playAgain) {
        ResultMessage msgDialog;
        String msg;
        if (gameResult > 0) {
            msg = player1Name.getText() + " wins";
        } else if (gameResult == 0) {
            msg = "Draw";
        } else {
            msg = player2Name.getText() + " wins";
        }
        setButtonOff(pass);
        setButtonOff(playAgain);
        msgDialog = new ResultMessage(GameVsHumanActivity.this, msg);
        msgDialog.show();
    }

    private Scores playerTurn(byte playerColor, LinearLayout player1Layout, LinearLayout player2Layout) {
        Scores scores = GameRules.getScores(gameBoard, playerColor);
        String player1Stats = X_TOKENS + scores.player1;
        String player2Stats = X_TOKENS + scores.player2;
        player1Tokens.setText(player1Stats);
        player2Tokens.setText(player2Stats);
        setPlayerColor(player1Layout, player2Layout);
        return scores;
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
            Intent intent = new Intent(GameVsHumanActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            GameVsHumanActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

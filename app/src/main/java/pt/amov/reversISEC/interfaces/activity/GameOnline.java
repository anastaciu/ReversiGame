package pt.amov.reversISEC.interfaces.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.amov.reversISEC.R;
import pt.amov.reversISEC.interfaces.dialog.PlayerInfoDialogBox;
import pt.amov.reversISEC.interfaces.views.GameView;
import pt.amov.reversISEC.logic.Constants;

public class GameOnline extends Activity implements Constants {
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
    private int player1TurnSpecialMoves = 0;
    private int player2TurnSpecialMoves = 0;
    private boolean player1PassNotUsed = true;
    private boolean player2PassNotUsed = true;
    private boolean player1TwiceSpecialPlayActive = false;
    private boolean player2TwiceSpecialPlayActive = false;
    private boolean player1TwiceNotUsed = true;
    private boolean player2TwiceNotUsed = true;


    private final byte[][] gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];


    ////dados do jogador
    PlayerInfoDialogBox player;

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

        //player = new PlayerInfoDialogBox(GameOnline.this, "xxx");
        //player.show();

        setButtonOff(playAgain);
        setButtonOff(pass);

        player1Name = findViewById(R.id.player1_name);
    }

    void setButtonOn(Button button) {
        button.setEnabled(true);
        button.setTextColor(getResources().getColor(R.color.design_default_color_primary_dark));
        button.setBackgroundResource(R.drawable.button_bg);
    }

    void setButtonOff(Button button) {
        button.setEnabled(false);
        button.setTextColor(getResources().getColor(R.color.WHITE));
        button.setBackgroundResource(R.drawable.button_disabled);
    }
}

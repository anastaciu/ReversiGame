package pt.amov.reversISEC.interfaces.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.R;


public class NewGameChooser extends Dialog implements Constants {

    private RadioButton black;
    private RadioButton white;
    private final RadioButton[] levelButtons = new RadioButton[3];
    private Button ok;

    public NewGameChooser(Context context, byte playColor, int difficulty) {

        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.new_game_dialog, null);

        black = view.findViewById(R.id.black);
        white = view.findViewById(R.id.white);

        if (playColor == BLACK) {
            black.setChecked(true);
        } else {
            white.setChecked(true);
        }

        levelButtons[0] = view.findViewById(R.id.level1);
        levelButtons[1] = view.findViewById(R.id.level2);
        levelButtons[2] = view.findViewById(R.id.level3);
        for (int i = 0; i < levelButtons.length; i++) {
            final int k = i;
            levelButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int index = 0; index < levelButtons.length; index++) {
                            if (index != k) {
                                levelButtons[index].setChecked(false);
                            }
                        }
                    }
                }
            });
        }
        levelButtons[difficulty - 1].setChecked(true);
        ok = view.findViewById(R.id.ok);
        super.setContentView(view);
    }

    public byte getPlayerColor() {
        return (black.isChecked() ? BLACK : WHITE);
    }

    public int getDifficulty() {
        for (int i = 0; i < levelButtons.length; i++) {
            if (levelButtons[i].isChecked()) {
                return i + 1;
            }
        }
        return 1;
    }

    public void setOnStartNewGameListener(View.OnClickListener onClickListener) {
        ok.setOnClickListener(onClickListener);
    }

}

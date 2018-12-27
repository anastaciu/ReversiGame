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



public class NewGameChooser extends Dialog {

    private RadioButton black;
    private final RadioButton[] radioButtons = new RadioButton[3];
    private Button ok;

    public NewGameChooser(Context context, byte playColor, int difficulty) {

        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.new_game_dialog, null);

        black = view.findViewById(R.id.black);
        RadioButton white = view.findViewById(R.id.white);
        if(playColor == Constants.BLACK){
            black.setChecked(true);
        }else{
            white.setChecked(true);
        }


        radioButtons[0] = view.findViewById(R.id.level1);
        radioButtons[1] = view.findViewById(R.id.level2);
        radioButtons[2] = view.findViewById(R.id.level3);
        for(int i = 0 ;i < radioButtons.length; i++){
            final int k = i;
            radioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        for(int index = 0; index < radioButtons.length; index++){
                            if(index != k){
                                radioButtons[index].setChecked(false);
                            }
                        }
                    }
                }
            });
        }
        radioButtons[difficulty - 1].setChecked(true);
        ok = view.findViewById(R.id.ok);
        super.setContentView(view);
    }

    public byte getPlayerColor(){
        return (black.isChecked()? Constants.BLACK:Constants.WHITE);
    }

    public int getDifficulty(){
        for(int i = 0; i < radioButtons.length; i++){
            if(radioButtons[i].isChecked()){
                return i+1;
            }
        }
        return 1;
    }

    public void setOnStartNewGameListener(View.OnClickListener onClickListener){
        ok.setOnClickListener(onClickListener);
    }

}

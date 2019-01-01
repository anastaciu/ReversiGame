package pt.amov.reversISEC.interfaces.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import pt.amov.reversISEC.R;
import pt.amov.reversISEC.logic.Constants;

public class ChooseGameMode extends Dialog implements Constants {

    private String gameMode;

    public ChooseGameMode(Context context, String gamesMode){
        super(context);
        this.gameMode = gamesMode;
        final ViewGroup nullParent = null;
        this.setCancelable(false);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.game_mode_box, nullParent);
        Button OnePlayer = view.findViewById(R.id.btn_1p_mode);
        Button twoPlayers = view.findViewById(R.id.btn_2p_mode);


        OnePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    dismiss();
                }

        });

        twoPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }

        });

        super.setContentView(view);
    }
}

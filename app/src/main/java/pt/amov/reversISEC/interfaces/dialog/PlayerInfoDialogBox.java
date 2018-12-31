package pt.amov.reversISEC.interfaces.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.amov.reversISEC.R;


public class PlayerInfoDialogBox extends Dialog {
    private TextView tvName;
    private EditText editText;

    public PlayerInfoDialogBox(Context context, TextView tName){
        super(context);
        final ViewGroup nullParent = null;
        this.tvName = tName;
        this.setCanceledOnTouchOutside(false);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_data_dialog, nullParent);
        Button ok = view.findViewById(R.id.btnOK1P);
        //Button foto = view.findViewById(R.id.foto);

        String playerName = "Player 1";
        this.tvName.setText(playerName);
        editText = view.findViewById(R.id.edName);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().length() > 0){
                    tvName.setText(String.valueOf(editText.getText()));
                    dismiss();
                }
                editText.setFocusable(true);
            }
        });

        super.setContentView(view);
    }


}

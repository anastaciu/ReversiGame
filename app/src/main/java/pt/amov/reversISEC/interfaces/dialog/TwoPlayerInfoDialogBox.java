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


public class TwoPlayerInfoDialogBox extends Dialog {
    private TextView tvNameP1;
    private TextView tvNameP2;
    private EditText editTextP1;
    private EditText editTextP2;

    public TwoPlayerInfoDialogBox(Context context, TextView tNameP1, TextView tNameP2){
        super(context);
        final ViewGroup nullParent = null;
        this.tvNameP1 = tNameP1;
        this.tvNameP2 = tNameP2;

        this.setCancelable(false);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.two_player_data_dialog, nullParent);
        Button ok = view.findViewById(R.id.btnOKTwoPlayers);
        //Button foto = view.findViewById(R.id.foto);

        editTextP1 = view.findViewById(R.id.edNameOne);
        editTextP2 = view.findViewById(R.id.edNameTwo);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextP1.getText().length() > 0 && editTextP2.getText().length() > 0){
                    tvNameP1.setText(String.valueOf(editTextP1.getText()));
                    tvNameP2.setText(String.valueOf(editTextP2.getText()));
                    dismiss();
                }
                editTextP1.setFocusable(true);
            }
        });

        super.setContentView(view);
    }


}

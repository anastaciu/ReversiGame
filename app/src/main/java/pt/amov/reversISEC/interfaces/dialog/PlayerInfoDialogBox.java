package pt.amov.reversISEC.interfaces.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import pt.amov.reversISEC.R;

public class PlayerInfoDialogBox extends Dialog {

    public PlayerInfoDialogBox(Context context, String msg) {

        super(context);
        final ViewGroup nullParent = null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_data_dialog, nullParent);
        Button ok = view.findViewById(R.id.ok);
        Button foto = view.findViewById(R.id.foto);
        TextInputLayout nameImput;

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        super.setContentView(view);
    }


}

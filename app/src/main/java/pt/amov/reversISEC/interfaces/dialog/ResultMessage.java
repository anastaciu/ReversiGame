package pt.amov.reversISEC.interfaces.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.view.ViewGroup;

import pt.amov.reversISEC.R;

public class ResultMessage extends Dialog {

    public ResultMessage(Context context, String msg) {

        super(context);
        final ViewGroup nullParent = null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.message_dialog, nullParent);
        TextView textView = view.findViewById(R.id.msg);
        textView.setText(msg);
        Button ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        super.setContentView(view);
    }




}

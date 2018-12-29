package pt.amov.reversISEC.interfaces.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import pt.amov.reversISEC.R;

public class RulesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_rule);
		TextView textView = findViewById(R.id.rule);
		textView.setText(getResources().getString(R.string.game_rule));
		if (Build.VERSION.SDK_INT > 26)
			textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RulesActivity.this.finish();
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

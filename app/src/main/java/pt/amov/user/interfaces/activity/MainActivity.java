package pt.amov.user.interfaces.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import pt.amov.game.Constants;
import pt.amov.reversi.R;


public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		Button play = findViewById(R.id.playButton);
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				byte playColor = (byte)preferences.getInt("playerColor", Constants.BLACK);
				int difficulty = preferences.getInt("difficulty", 1);

				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				Bundle bundle = new Bundle();
				bundle.putByte("playerColor", playColor);
				bundle.putInt("difficulty", difficulty);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			}
		});

		Button rule = findViewById(R.id.ruleButton);
		rule.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, GameRuleActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

}

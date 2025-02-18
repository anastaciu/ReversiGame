package pt.amov.reversISEC.interfaces.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import pt.amov.reversISEC.interfaces.dialog.NewGameChooser;
import pt.amov.reversISEC.logic.Constants;
import pt.amov.reversISEC.R;


public class MainActivity extends Activity implements Constants{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Button playVsAi = findViewById(R.id.playVsAiButton);
        playVsAi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        byte playColor = (byte) preferences.getInt("playerColor", -1);
                                        int difficulty = preferences.getInt("difficulty", 1);
                                        Intent intent = new Intent(MainActivity.this, GameVsAiActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putByte("playerColor", playColor);
                                        bundle.putInt("difficulty", difficulty);
                                        intent.putExtra("gameMode", 1);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    }
                                }


        );

        Button playVsHuman = findViewById(R.id.playVsHumanButton);
        playVsHuman.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {


                                         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                         byte playColor = (byte)preferences.getInt("playerColor", -1);
                                         byte gameMode = (byte)preferences.getInt("game", 2);
                                         int difficulty = preferences.getInt("difficulty", 1);
                                         Intent intent = new Intent(MainActivity.this, GameVsHumanActivity.class);
                                         Bundle bundle = new Bundle();
                                         bundle.putByte("game", gameMode);
                                         bundle.putByte("playerColor", playColor);
                                         bundle.putInt("difficulty", difficulty);
                                         intent.putExtras(bundle);
                                         startActivity(intent);
                                         overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                     }
                                 }


        );

        Button playOnline = findViewById(R.id.playOnline);
        playOnline.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Toast.makeText(MainActivity.this,
                                                       "Disponível na versão 2.0", Toast.LENGTH_LONG).show();

                                           }
                                       }


        );

        Button rules = findViewById(R.id.ruleButton);
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RulesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
    }

}

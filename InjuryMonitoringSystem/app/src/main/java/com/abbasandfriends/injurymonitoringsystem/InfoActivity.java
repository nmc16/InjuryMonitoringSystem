package com.abbasandfriends.injurymonitoringsystem;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class InfoActivity extends Activity  {
    TextView playerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        playerName = (TextView) findViewById(R.id.textView5);
        String currName = MainAppActivity.currentName;
        playerName.setText(currName);


    }

}

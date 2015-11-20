package com.abbasandfriends.injurymonitoringsystem;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class Main2Activity extends Activity  {
    TextView playerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        playerName = (TextView) findViewById(R.id.textView5);
        String currName = MainActivity.currentName;
        playerName.setText(currName);


    }

}

package com.abbasandfriends.injurymonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends android.app.Activity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    Button prevWarn;
    Button graph;
    Button emerg;
    static String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner= (Spinner) findViewById((R.id.spinner));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.players, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        graph = (Button) findViewById(R.id.graph);
        prevWarn = (Button) findViewById(R.id.prevWarn);
        emerg = (Button) findViewById(R.id.emerg);

        graph.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Main3Activity.class);
                startActivity(i);
            }

        });

        prevWarn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(i);

            }

        });

        emerg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Emergency Pressed", Toast.LENGTH_SHORT).show();
            }

        });






    }
    //Testing to see if it works-Remove once complete
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView playerName = (TextView) view;
        Toast.makeText(this, "Selected " +playerName.getText(), Toast.LENGTH_SHORT).show();
        CharSequence playerChar = playerName.getText();
        String playerSelected = playerChar.toString();
        currentName = playerSelected;



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

/*
    step 1: create the data source
    step 2: define the appearance layout file through which the adapter will put the data inside the spinner
    step 3: define what to do when the user clicks on the spinner using the OnItemSelectedListener


    Requests: Acceleration
    Requests: Position
    Requests: Alarm
    Sends: Emergency
     */

}

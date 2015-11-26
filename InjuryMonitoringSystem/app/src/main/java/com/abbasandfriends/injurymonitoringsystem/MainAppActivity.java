package com.abbasandfriends.injurymonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;


/**
 * Main class that manages first Activity and creates other activities for the Android GUI.
 *
 * Creates all buttons and a dropdown spinner for use on the Main activity and
 * creates listeners for the buttons to activate secondary activities when pressed.
 *
 * @version 2
 */
public class MainAppActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public static String currentName;

    /**
     * Method that creates the main activity view and links its widgets to their listeners.
     * Declares all buttons and the spinner that are used in the view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spinner;
        final Button warningInfo;
        final Button graph;
        final Button emerg;

        spinner= (Spinner) findViewById((R.id.spinner));

        //Adapter links the xml array to the spinner and sets it as dropdown
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.players, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        graph = (Button) findViewById(R.id.graph);
        warningInfo = (Button) findViewById(R.id.prevWarn);
        emerg = (Button) findViewById(R.id.emerg);

        //when the graph button is pressed, switch content view to to the graph activity
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainAppActivity.this, GraphActivity.class);
                startActivity(i);
            }
        });

        //when the warningInfo button is pressed, switch the content view to the Information activity
        warningInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainAppActivity.this, InfoActivity.class);
                startActivity(i);
            }
        });

        //when the emerg button is pressed, toast the screen
        emerg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Emergency Pressed", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * When a name is selected in the spinner,
     * a toast is created onscreen declaring the name selected.
     *
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView playerName = (TextView) view;
        Toast.makeText(this, "Selected " + playerName.getText(), Toast.LENGTH_SHORT).show();
        CharSequence playerChar = playerName.getText();

        //The selected name is assigned to a string variable to be used in the secondary activities
        currentName = playerChar.toString();



    }

    /**
     * If nothing is selected, toast the screen
     *
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
    }

}
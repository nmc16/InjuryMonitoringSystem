package com.abbasandfriends.injurymonitoringsystem;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.abbasandfriends.injurymonitoringsystem.alarm.AlarmDialog;
import com.abbasandfriends.injurymonitoringsystem.request.RequestDialog;

import java.util.ArrayList;
import java.util.List;

import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.PlayerCause;


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
    private static List<Sendable> data;
    private static TableLayout table;

    /**
     * Method that creates the main activity view and links its widgets to their listeners.
     * Declares all buttons and the spinner that are used in the view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        data = new ArrayList<Sendable>();

        setContentView(R.layout.activity_main);

        final Spinner spinner;
        final Button warningInfo;
        final Button graph;
        final Button emerg;
        final Button request;
        final Button setupButton;
        table = (TableLayout) findViewById(R.id.dataTable);

        spinner = (Spinner) findViewById((R.id.spinner));

        //Adapter links the xml array to the spinner and sets it as dropdown
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.players, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        graph = (Button) findViewById(R.id.graph);
        warningInfo = (Button) findViewById(R.id.prevWarn);
        emerg = (Button) findViewById(R.id.emerg);
        request = (Button) findViewById(R.id.requestButton);
        setupButton = (Button) findViewById(R.id.setupButton);

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
                AlertDialog alertDialog = new AlarmDialog(MainAppActivity.this).
                        create(new Alarm(10, System.currentTimeMillis(), new PlayerCause(10)));
                alertDialog.show();
            }

        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDialog requestDialog = new RequestDialog(MainAppActivity.this);
                AlertDialog alertDialog = requestDialog.create();
                alertDialog.show();
            }
        });

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainAppActivity.this, ConnectionsActivity.class);
                startActivity(i);
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
        if (view != null && parent != null) {
            TextView playerName = (TextView) view;
            Toast.makeText(this, "Selected " + playerName.getText(), Toast.LENGTH_SHORT).show();
            CharSequence playerChar = playerName.getText();

            //The selected name is assigned to a string variable to be used in the secondary activities
            currentName = playerChar.toString();
        }



    }

    /**
     * If nothing is selected, toast the screen
     *
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
    }

    public static void addData(List<Sendable> sendables) {
        data.addAll(sendables);

    }
}

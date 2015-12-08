package com.abbasandfriends.injurymonitoringsystem;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.abbasandfriends.injurymonitoringsystem.alarm.AlarmDialog;
import com.abbasandfriends.injurymonitoringsystem.async.AsyncConnectionSetup;
import com.abbasandfriends.injurymonitoringsystem.async.AsyncListener;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;
import com.abbasandfriends.injurymonitoringsystem.request.RequestDialog;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import exception.CommunicationException;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.Cause;

/**
 * Main class that manages first Activity and creates other activities for the Android GUI.
 *
 * Creates all buttons and a dropdown spinner for use on the Main activity and
 * creates listeners for the buttons to activate secondary activities when pressed.
 *
 * @version 3
 */
public class MainAppActivity extends Activity implements AdapterView.OnItemSelectedListener, AsyncListener {
    public static final String HOST_IP = "hostip";
    public static final String HOST_PORT = "hostport";
    public static final String LOG_TAG = "MainAppActivity";
    public static String currentName;
    public static List<Sendable> data;
    private static TableLayout table;
    public static boolean dialogFlag = false;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    /**
     * Method that creates the main activity view and links its widgets to their listeners.
     * Declares all buttons and the spinner that are used in the view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialogFlag = false;
        data = new ArrayList<Sendable>();

        final Spinner spinner;
        final Button setupButton = (Button) findViewById(R.id.setupButton);
        final Button graph = (Button) findViewById(R.id.graph);
        final Button warningInfo = (Button) findViewById(R.id.prevWarn);
        final Button emerg = (Button) findViewById(R.id.emerg);
        final Button request = (Button) findViewById(R.id.requestButton);

        table = (TableLayout) findViewById(R.id.dataTable);

        //Animations for unclicked buttons
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        setupButton.startAnimation(animation);

        spinner = (Spinner) findViewById((R.id.spinner));

        //Adapter links the xml array to the spinner and sets it as dropdown
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.players, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


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
                        create(new Alarm(1, System.currentTimeMillis(),
                                new Cause("Request emergency for player!")));
                mediaStart();
                alertDialog.show();
            }

        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDialog requestDialog = new RequestDialog(MainAppActivity.this,
                                                                MainAppActivity.this);
                AlertDialog alertDialog = requestDialog.create();
                alertDialog.show();
            }
        });

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                Intent i = new Intent(MainAppActivity.this, ConnectionsActivity.class);
                startActivityForResult(i, 1);
            }
        });
    }

    /**
     * When a name is selected in the spinner,
     * a toast is created onscreen declaring the name selected.
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the data sent back is valid
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                // Get the ip and port from the last activity
                String hostIP = data.getStringExtra(HOST_IP);
                String hostPort = data.getStringExtra(HOST_PORT);

                // Create the parameter list and start connection setup
                String params[] = {hostIP, hostPort};
                new AsyncConnectionSetup().execute(params);

                // Start the receive thread
                new AsyncReceive().execute();
            }
        }
    }
    public void mediaStart(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sirensound);
        mp.start();
    }

    /**
     * If nothing is selected, toast the screen
     *
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
    }
    
    /*
    * Assigns data to a scrollable listview on the MainAppActivity
    */
    public void addData(Sendable sendable) {
        // Add to the data
        data.add(sendable);

        // Add a table row for the new data
        TableRow tableRow = new TableRow(MainAppActivity.this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);

        // Add the UID
        TextView uid = new TextView(MainAppActivity.this);
        uid.setLayoutParams(new TableRow.LayoutParams(50, 60, 1));
        uid.setText(Integer.toString(sendable.getUID()));
        uid.setGravity(Gravity.CENTER);
        tableRow.addView(uid);

        // Add Type
        TextView type = new TextView(MainAppActivity.this);
        type.setLayoutParams(new TableRow.LayoutParams(50, 60, 1));
        type.setText(Integer.toString(sendable.getType()));
        type.setGravity(Gravity.CENTER);
        tableRow.addView(type);

        // Add the values of the sendable object from toString
        TextView data = new TextView(MainAppActivity.this);
        data.setLayoutParams(new TableRow.LayoutParams(50, 60, 1));
        data.setText(sendable.toString());
        data.setGravity(Gravity.CENTER);
        tableRow.addView(data);

        // Add the time to the table
        TextView date = new TextView(MainAppActivity.this);
        date.setLayoutParams(new TableRow.LayoutParams(50, 60, 1));
        date.setText(dateFormat.format(sendable.getDate()));
        date.setGravity(Gravity.CENTER);
        tableRow.addView(date);

        table.addView(tableRow);
    }

    private class AsyncReceive extends AsyncTask<Void, Void, List<Sendable>> {
        private static final String LOG_TAG = "AsyncReceive";
        private AlarmDialog alarmDialog;
        private boolean errorFlag = false;

        @Override
        protected List<Sendable> doInBackground(Void... params) {
            // Attempt to get the connection from context
            Object o = ContextHandler.get(ContextHandler.HANDLER);

            // If it is valid, then attempt to send the request
            try {
                // Check the object is valid
                if (o == null || !(o instanceof ConnectionHandler)) {
                    Log.e(LOG_TAG, "Could not get connection handler from Context!");
                    Thread.sleep(5000);
                    return null;
                }

                ConnectionHandler connectionHandler = (ConnectionHandler) o;
                Socket s = connectionHandler.getDataBaseReceive();
                if (s == null) {
                    Log.e(LOG_TAG, "Socket for database connection is null");
                    Thread.sleep(5000);
                    return null;
                }
                List<Sendable> received = connectionHandler.receive(s);
                return received;
            } catch (CommunicationException e) {
                Log.e(LOG_TAG, "Exception during receive: " + e.getLocalizedMessage() + "\n Cause: " +
                        e.getCause().getMessage());
                e.printStackTrace();
                errorFlag = true;
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Interrupted during thread sleep!");
            }

            return null;
        }

        private void parse(List<Sendable> list) {
            if (list == null || list.isEmpty()) {
                return;
            }

            Log.d(LOG_TAG, "Received data from database...");
            for (Sendable sendable : list) {
                if (sendable instanceof Alarm) {
                    displayAlarm((Alarm) sendable);
                }
            }
        }

        private void displayAlarm(Alarm alarm) {
            if (!dialogFlag) {
                alarmDialog.create(alarm).show();
                dialogFlag = true;
                mediaStart();
            }
        }

        @Override
        protected void onPostExecute(List<Sendable> sendables) {
            super.onPostExecute(sendables);
            parse(sendables);

            if (!errorFlag) {
                new AsyncReceive().execute();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alarmDialog = new AlarmDialog(MainAppActivity.this);
        }
    }
}

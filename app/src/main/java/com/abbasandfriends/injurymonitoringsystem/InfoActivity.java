package com.abbasandfriends.injurymonitoringsystem;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import java.util.List;

import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.TrainerCause;

/**
 * Activity that displays the alarm information for the players.
 */
public class InfoActivity extends Activity  {
    private static final int CHARLIE = 1;
    private static final int LUKE = 2;
    private static final int NIC = 3;
    private static final int ABBAS = 4;
    TextView playerName;
    TextView severity;
    TextView impacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Add the text views for the player information
        playerName = (TextView) findViewById(R.id.textView5);
        String currName = MainAppActivity.currentName;
        playerName.setText(currName);

        // Set the id from the array of players
        int id = 0;
        if (currName.equals("Charlie")) {
            id = 1;
        } else if (currName.equals("Luke")) {
            id = 2;
        } else if (currName.equals("Nic")) {
            id = 3;
        } else {
            id = 4;
        }

        // Add the text view for the severity and impacts
        severity = (TextView) findViewById(R.id.textView6);
        impacts = (TextView) findViewById(R.id.textView7);

        int playerImpacts = 0;

        // Get the data from the main activity
        List<Sendable> data = MainAppActivity.data;
        for (Sendable sendable : data) {
            // If the data type is an alarm add one to the impacts
            if (sendable instanceof Alarm) {
                if (sendable.getUID() == id) {
                    playerImpacts++;
                }

                // If there is a trainer cause update the priority
                if (((Alarm) sendable).getCause() instanceof TrainerCause) {
                    severity.setText(((TrainerCause) ((Alarm) sendable).getCause()).getPriority().toString());
                }
            }
        }

        // Set the text to the value of total impacts
        impacts.setText(String.valueOf(playerImpacts));
    }

}

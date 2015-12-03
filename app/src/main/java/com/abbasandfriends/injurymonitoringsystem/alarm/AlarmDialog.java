package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.app.Activity;
import android.app.AlertDialog;

import com.abbasandfriends.injurymonitoringsystem.MainAppActivity;
import com.abbasandfriends.injurymonitoringsystem.R;

import sendable.alarm.Alarm;
import sendable.alarm.DataCause;
import sendable.alarm.PlayerCause;

/**
 * Dialog box to pop up once a Alarm comes from the data base
 * when a threshold is crossed for a player.
 *
 * @version 1
 */
public class AlarmDialog {
    private AlertDialog.Builder builder;

    public AlarmDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity);
    }

    /**
     * Creates a dialog box using the custom listeners {@link RequestListener}.
     * and the {@link Alarm} object passed.
     *
     * Will send a request to the database for emergency assistance if the
     * request button is pressed.
     *
     * Otherwise it will dismiss the dialog box.
     * @param alarm @{link Alarm} object to create the message from
     * @return Dialog box containing the alarm that can be shown
     */
    public AlertDialog create(Alarm alarm) {
        // Set the title of the dialog box
        builder.setTitle(R.string.alarm_dialog_title);
        String currentPlayer;


        // Create a new string based on the cause of the alarm
        StringBuilder sb = new StringBuilder("Cause: ");
        if (alarm.getCause() instanceof PlayerCause) {
            //hardcoded for simplicity
            //In reality an arraylist initializer would be preferred
            if (((PlayerCause) alarm.getCause()).getPlayerID() == 1){
                currentPlayer = "Charlie";
            } else if (((PlayerCause) alarm.getCause()).getPlayerID() == 2){
                currentPlayer = "Luke";
            } else if (((PlayerCause) alarm.getCause()).getPlayerID() == 3){
                currentPlayer = "Nic";
            } else if (((PlayerCause) alarm.getCause()).getPlayerID() == 4){
                currentPlayer = "Abbas";
            } else if (((PlayerCause) alarm.getCause()).getPlayerID() == 10){
                currentPlayer = "Trainer";
            }
            else {
                currentPlayer = (((PlayerCause) alarm.getCause()).getPlayerID()) + "";
            }
            sb.append("Player ").append(currentPlayer).append(" pressed emergency button!");

        } else if (alarm.getCause() instanceof DataCause) {
            sb.append("Player (").append(alarm.getUID()).append(") crossed ").append(((DataCause) alarm.getCause()).getThreshold())
                    .append(" threshold at time ").append(alarm.getDate()).append("!");
        } else {
            sb.append("Unknown alarm cause");
        }


        // Display the message created from the alarm data
        builder.setMessage(sb);

        // Set the custom listeners in the dialog box
        builder.setPositiveButton(R.string.alarm_dialog_request, new RequestListener());
        builder.setNegativeButton(R.string.alarm_dialog_dismiss, new DismissClickListener());
        return builder.create();
    }
}

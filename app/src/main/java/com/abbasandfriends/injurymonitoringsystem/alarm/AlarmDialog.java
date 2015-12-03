package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.app.Activity;
import android.app.AlertDialog;

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

        // Create a new string based on the cause of the alarm
        StringBuilder sb = new StringBuilder("Cause: ");
        if (alarm.getCause() instanceof PlayerCause) {
           sb.append("Player ").append(((PlayerCause) alarm.getCause()).getPlayerID())
                   .append(" pressed emergency button!");
        } else if (alarm.getCause() instanceof DataCause) {
            sb.append("Data crossed ").append(((DataCause) alarm.getCause()).getThreshold())
                    .append(" threshold!");
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

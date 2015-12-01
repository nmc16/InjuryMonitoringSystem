package com.abbasandfriends.injurymonitoringsystem.request;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import sendable.alarm.Alarm;
import sendable.data.Acceleration;
import sendable.data.Position;

public class RequestClickListener implements View.OnClickListener {
    private final EditText startTime;
    private final EditText endTime;
    private final EditText playerID;
    private final Spinner spinner;
    private final Activity activity;
    private long userStartTime;
    private long userEndTime;
    private int userPlayerID;
    private Class selectedClass = null;

    private final Dialog dialog;
    public RequestClickListener(Dialog dialog, EditText startTime, EditText endTime,
                                EditText playerID, Spinner spinner, Activity activity) {
        this.dialog = dialog;
        this.startTime = startTime;
        this.endTime = endTime;
        this.playerID = playerID;
        this.spinner = spinner;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        // Check the start time entered and set to -1 if the user does not want one
        if (startTime.getText().toString().isEmpty()) {
            userStartTime = -1;
        } else {
            userStartTime = Integer.valueOf(startTime.getText().toString());
        }

        // Check the end time entered and set to -1 if the user does not want one
        if (endTime.getText().toString().isEmpty()) {
            userEndTime = -1;
        } else {
            userEndTime = Long.valueOf(endTime.getText().toString());
        }

        // Store the class instance from the selected spinner option
        // TODO change this to DataType.? after merge
        if (spinner.getSelectedItem().toString().equals("Position")) {
            selectedClass = Position.class;
        } else if (spinner.getSelectedItem().toString().equals("Acceleration")) {
            selectedClass = Acceleration.class;
        } else {
            selectedClass = Alarm.class;
        }

        // Check that the user entered a valid player id
        if (!playerID.getText().toString().isEmpty()) {
            userPlayerID = Integer.valueOf(playerID.getText().toString());
            dialog.dismiss();
        } else {
            Toast.makeText(activity, "Please enter valid player ID", Toast.LENGTH_SHORT).show();

        }
    }

    // TODO create request return after merged with master
    /*
     * public Request getRequest() {
     *     return new Request(userPlayerID, selectedClass, userStartTime, userEndTime);
     * }
     *
     */
}

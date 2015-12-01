package com.abbasandfriends.injurymonitoringsystem.request;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.abbasandfriends.injurymonitoringsystem.MainAppActivity;

import sendable.DataType;
import sendable.data.Request;

/**
 * Implementation of {@link android.view.View.OnClickListener} that provides
 * uses the {@link RequestDialog} dialog box and the input fields from it
 * to create a request object.
 *
 * Calls the {@link MainAppActivity#requestData(Request)} method with the
 * request data after the valid input has been entered.
 *
 * @version 1
 */
public class RequestClickListener implements View.OnClickListener {
    private final EditText startTime;
    private final EditText endTime;
    private final EditText playerID;
    private final Spinner spinner;
    private final Activity activity;
    private final Dialog dialog;
    private Request request;

    public RequestClickListener(Dialog dialog, EditText startTime, EditText endTime,
                                EditText playerID, Spinner spinner, Activity activity) {
        this.dialog = dialog;
        this.startTime = startTime;
        this.endTime = endTime;
        this.playerID = playerID;
        this.spinner = spinner;
        this.activity = activity;
        this.request = new Request();
    }

    @Override
    public void onClick(View v) {
        // Check the start time entered and set to -1 if the user does not want one
        if (startTime.getText().toString().isEmpty()) {
            request.setStartTime(-1);
        } else {
            request.setStartTime(Integer.valueOf(startTime.getText().toString()));
        }

        // Check the end time entered and set to -1 if the user does not want one
        if (endTime.getText().toString().isEmpty()) {
            request.setEndTime(-1);
        } else {
           request.setEndTime(Long.valueOf(endTime.getText().toString()));
        }

        // Store the class instance from the selected spinner option
        if (spinner.getSelectedItem().toString().equals("Position")) {
            request.setRequestType(DataType.POS);
        } else if (spinner.getSelectedItem().toString().equals("Acceleration")) {
            request.setRequestType(DataType.ACCEL);
        } else {
            request.setRequestType(DataType.ALARM);
        }

        // Check that the user entered a valid player id
        if (!playerID.getText().toString().isEmpty()) {
            request.setUID(Integer.valueOf(playerID.getText().toString()));
            request.setTime(System.currentTimeMillis());

            // Dismiss the dialog box
            dialog.dismiss();

            // Send a request from the main activity
            MainAppActivity.requestData(request);
        } else {
            Toast.makeText(activity, "Please enter valid player ID", Toast.LENGTH_SHORT).show();
        }
    }
}

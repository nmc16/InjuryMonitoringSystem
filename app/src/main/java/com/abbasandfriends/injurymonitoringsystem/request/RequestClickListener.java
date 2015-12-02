package com.abbasandfriends.injurymonitoringsystem.request;


import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.abbasandfriends.injurymonitoringsystem.ContextHandler;
import com.abbasandfriends.injurymonitoringsystem.MainAppActivity;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.Socket;

import exception.CommunicationException;
import sendable.DataType;
import sendable.data.Request;

/**
 * Implementation of {@link android.view.View.OnClickListener} that provides
 * uses the {@link RequestDialog} dialog box and the input fields from it
 * to create a request object.
 *
 * Sends request to database and lets the retrieve thread handle
 * the incoming request.
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
            sendRequest();
        } else {
            Toast.makeText(activity, "Please enter valid player ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest() {
        // Attempt to get the connection from context
        Object o = ContextHandler.get(ContextHandler.HANDLER);

        // Check the object is valid
        if (o == null || !(o instanceof ConnectionHandler)) {
            Toast.makeText(activity, "You must set-up the connections before you can send request!",
                           Toast.LENGTH_SHORT).show();
            return;
        }

        // If it is valid, then attempt to send the request
        try {
            ConnectionHandler connectionHandler = (ConnectionHandler) o;
            Socket s = connectionHandler.getDataBaseRequest();
            connectionHandler.send(request, s);
            Log.d("Request", "Request sent to database");
        } catch (CommunicationException e) {
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}

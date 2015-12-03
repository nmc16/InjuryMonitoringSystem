package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.content.DialogInterface;
import android.util.Log;

import com.abbasandfriends.injurymonitoringsystem.ContextHandler;
import com.abbasandfriends.injurymonitoringsystem.MainAppActivity;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.Socket;

import exception.CommunicationException;
import sendable.alarm.Alarm;
import sendable.alarm.Priority;
import sendable.alarm.TrainerCause;

/**
 * Click listener that will make a request for emergency response by sending
 * an Alarm object to the database.
 *
 * @version 1
 */
public class RequestListener implements DialogInterface.OnClickListener {
    private static final String LOG_TAG = "RequestListener";

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Reset the flag on the main app activity
        MainAppActivity.dialogFlag = false;

        // Get the connection handler from the context
        Object o = ContextHandler.get(ContextHandler.HANDLER);

        // Check valid connection handler returned
        if(o == null || !(o instanceof ConnectionHandler)) {
            Log.e(LOG_TAG, "Could not get connection handler!");
            dialog.dismiss();
            return;
        }

        // Send the alarm to the database
        ConnectionHandler connectionHandler = (ConnectionHandler) o;
        Alarm alarm = new Alarm(420, System.currentTimeMillis(), new TrainerCause(Priority.MODERATE));
        Log.d(LOG_TAG, "Sending the alarm to the database...");
        try {
            Socket socket = connectionHandler.getDataBaseRequest();
            connectionHandler.send(alarm, socket);
            Log.d(LOG_TAG, "Sent alarm.");
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "Could not send the alarm: " + e.getLocalizedMessage() + "\n" +
                           "Cause: " + e.getCause().getMessage());
        }

        dialog.dismiss();
    }
}

package com.abbasandfriends.injurymonitoringsystem.async;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.abbasandfriends.injurymonitoringsystem.ContextHandler;
import com.abbasandfriends.injurymonitoringsystem.alarm.AlarmDialog;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.Socket;
import java.util.List;

import exception.CommunicationException;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.data.Service;

public class AsyncReceive extends AsyncTask<Activity, Void, Void> {
    private static final String LOG_TAG = "AsyncReceive";
    private Activity params[];

    @Override
    protected Void doInBackground(Activity... params) {
        this.params = params;
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
            parse(received, params[0]);
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "Could not get connection handler from Context!");
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Interrupted during thread sleep!");

        }

        return null;
    }

    private void parse(List<Sendable> list, Activity activity) {
        for (Sendable sendable : list) {
            if (sendable instanceof Alarm) {
                displayAlarm((Alarm) sendable, activity);
            } else if (sendable instanceof Service) {
                decodeService(sendable);
            } else {
                // Do nothing
            }
        }
    }

    private void displayAlarm(Alarm alarm, Activity activity) {
        AlertDialog alertDialog = new AlarmDialog(activity).create(alarm);
        alertDialog.show();
    }

    @SuppressWarnings("unchecked")
    private void decodeService(Sendable sendable) {
        Service service = (Service) sendable;
        List<Sendable> list = service.getData();
        Log.d("AsyncReceive", list.toString());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        new AsyncReceive().execute(params);
    }
}

package com.abbasandfriends.injurymonitoringsystem.async;


import android.os.AsyncTask;
import android.util.Log;

import com.abbasandfriends.injurymonitoringsystem.ContextHandler;
import com.abbasandfriends.injurymonitoringsystem.MainAppActivity;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.Socket;
import java.util.List;

import exception.CommunicationException;
import sendable.Sendable;
import sendable.data.Request;
import sendable.data.Service;

public class AsyncRequest extends AsyncTask<Request, Void, List<Sendable>> {
    private static final String LOG_TAG = "AsyncRequest";

    @Override
    protected List<Sendable> doInBackground(Request... params) {
        Log.d(LOG_TAG, "Started...");
        // Attempt to get the connection from context
        Object o = ContextHandler.get(ContextHandler.HANDLER);

        // If it is valid, then attempt to send the request
        try {
            // Check the object is valid
            if (o == null || !(o instanceof ConnectionHandler)) {
                Log.e(LOG_TAG, "Could not get connection handler from Context!");
                return null;
            }

            // Get the request socket and read it for data
            ConnectionHandler connectionHandler = (ConnectionHandler) o;
            Socket s = connectionHandler.getDataBaseRequest();

            Log.d(LOG_TAG, "Sending request: " + params[0].getRequestType());
            connectionHandler.send(params[0], s);
            Log.d(LOG_TAG, "Request sent to database");

            Log.d(LOG_TAG, "Waiting for data...");
            List<Sendable> received = connectionHandler.receive(s);
            Log.d(LOG_TAG, "Received data...");
            return received;
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "Exception during receive: " + e.getLocalizedMessage() + "\n Cause: " +
                    e.getCause().getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Sendable> sendables) {
        super.onPostExecute(sendables);

        for (Sendable sendable : sendables) {
            if (sendable instanceof Service) {
                decodeService(sendable);
            }
        }
    }

    private void decodeService(Sendable sendable) {
        Service service = (Service) sendable;
        List<?> received = service.getData();

        for (Object o : received) {
            if (o instanceof Sendable) {
                Sendable s = (Sendable) o;
                MainAppActivity.addData(s);
            }
        }
    }
}

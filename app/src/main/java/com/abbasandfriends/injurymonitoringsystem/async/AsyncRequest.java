package com.abbasandfriends.injurymonitoringsystem.async;


import android.os.AsyncTask;
import android.util.Log;

import com.abbasandfriends.injurymonitoringsystem.ContextHandler;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.Socket;
import java.util.List;

import exception.CommunicationException;
import sendable.Sendable;
import sendable.data.Request;
import sendable.data.Service;

/**
 * Class to handle asynchronous requests to the database for data.
 *
 * The parameters must be ordered specifically:
 *      param[0]: the request object created to send to the database
 *      param[1]: the AsyncListener implementation to call addData on
 */
public class AsyncRequest extends AsyncTask<Object, Void, List<Sendable>> {
    private static final String LOG_TAG = "AsyncRequest";
    private AsyncListener listener;

    @Override
    protected List<Sendable> doInBackground(Object... params) {
        // Check that the parameters passed are correct
        if (params.length < 2 || !(params[0] instanceof Request) ||
                                 !(params[1] instanceof AsyncListener)) {
            Log.e(LOG_TAG, "Parameters passed were not correct!");
            return null;
        }

        listener = (AsyncListener) params[1];

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
            Request request = (Request) params[0];

            Log.d(LOG_TAG, "Sending request: " + request.getRequestType());
            connectionHandler.send(request, s);
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
                listener.addData(s);
            }
        }
    }
}

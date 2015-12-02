package com.abbasandfriends.injurymonitoringsystem.async;


import android.os.AsyncTask;
import android.util.Log;

import com.abbasandfriends.injurymonitoringsystem.ContextHandler;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import exception.CommunicationException;

public class AsyncConnectionSetup extends AsyncTask<String, Void, Boolean> {
    private static final String LOG_TAG = "AsyncConnectionSetup";

    @Override
    protected Boolean doInBackground(String... params) {
        ConnectionHandler connectionHandler = new ConnectionHandler();

        int hostPort = Integer.valueOf(params[1]);
        int clientPort = Integer.valueOf(params[3]);

        // Set up the connections to the database and the client connection to database
        try {
            // Set up the request socket for the connection
            Log.d(LOG_TAG, "Setting up receive connection...");
            Socket r = connectionHandler.connectTo(params[2], clientPort);
            connectionHandler.setDataBaseRequest(r);

            // Set up the host connection for incoming alarms
            Log.d(LOG_TAG, "Setting up host connection...");
            InetAddress inetAddress = InetAddress.getByName(params[0]);
            Log.d(LOG_TAG, "Connecting to IP " + params[0]);
            connectionHandler.host(hostPort, inetAddress);
            Socket s = connectionHandler.acceptClient();
            connectionHandler.setDataBaseReceive(s);

            // Add the connection handler to the context
            ContextHandler.add(ContextHandler.HANDLER, connectionHandler);
            Log.d(LOG_TAG, "Finished.");
            return true;

        } catch (UnknownHostException e) {
            Log.e(LOG_TAG, "Could not create host IP: " + e.getLocalizedMessage());
            return false;
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "Could not set up connections: " + e.getLocalizedMessage());
            Log.e(LOG_TAG, "Cause: " + e.getCause().getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }
}

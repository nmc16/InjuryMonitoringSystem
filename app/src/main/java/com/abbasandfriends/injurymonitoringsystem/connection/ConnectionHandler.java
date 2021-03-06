package com.abbasandfriends.injurymonitoringsystem.connection;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import controller.Consumer;
import controller.Producer;
import exception.CommunicationException;
import json.SendableDeserializer;
import sendable.Sendable;

public class ConnectionHandler implements Consumer, Producer {
    private static final int BUFFER_SIZE = 131072;
    private static final String LOG_TAG = "ConnectionHandler";
    private Socket server;
    private Socket dataBaseReceive;
    private Socket dataBaseRequest;
    private Gson gson;

    public ConnectionHandler() {
        gson = new GsonBuilder().registerTypeAdapter(Sendable.class, new SendableDeserializer()).create();
    }

    @Override
    public void host(int hostPort, InetAddress ip) throws CommunicationException {
        // Check there is not a host already open
        if (server != null && !server.isClosed()) {
            throw new CommunicationException("Socket not closed before trying to host!");
        }

        // Start server
        try {
            server = new Socket(ip, hostPort);
        } catch (IOException e) {
            throw new CommunicationException("Could not open server socket or input stream", e);
        }
    }

    @Override
    public Socket acceptClient() throws CommunicationException {
        if (server == null || server.isClosed()) {
            throw new CommunicationException("Host socket is closed!");
        }

        return server;
    }

    @Override
    public List<Sendable> receive(Socket clientSocket) throws CommunicationException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte tempBuffer[] = new byte[BUFFER_SIZE];
        List<Sendable> received = new ArrayList<Sendable>();

        try {
            clientSocket.setSoTimeout(2000);

            // Get the input stream from the socket and read it into a string
            InputStream inputStream = clientSocket.getInputStream();

            int result = inputStream.read(tempBuffer);
            while (result != -1) {
                buffer.write(tempBuffer, 0, result);
                result = inputStream.read(tempBuffer);
            }
        } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException)) {
                throw new CommunicationException("Could not read from input buffer or timed out", e);
            }
        }

        String msg = new String(buffer.toByteArray());
        msg = msg.replace("\u0000", "").replace("\\u0000", "");

        // Create readers to separate objects
        JsonReader jsonReader = new JsonReader(new StringReader(msg));
        jsonReader.setLenient(true);

        // If the message is null then return the empty list
        if (msg.length() == 0) {
             return received;
        }

        Log.d(LOG_TAG, "Received: " + msg);
        try {
            while (jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                Sendable sendable = gson.fromJson(jsonReader, Sendable.class);
                received.add(sendable);
            }
        } catch(JsonSyntaxException e){
                // Catch error with full buffer, one of the data points will be lost
                Log.e(LOG_TAG, "JSON could not be parsed, buffer may have overflown: " +
                        e.getMessage());
                Log.e(LOG_TAG, "May have lost data points from request!");
        } catch (IOException e) {
            throw new CommunicationException("Could not parse the JSON message", e);
        }

        return received;
    }

    @Override
    public void disconnectHost() throws CommunicationException {
        // Check the host exists
        if (server == null) {
            throw new CommunicationException("There is no server to disconnect!");
        }

        // Attempt to close the host connection
        try {
            server.close();
        } catch (IOException e) {
            throw new CommunicationException("Could not close server socket", e);
        }
    }

    @Override
    public Socket connectTo(String clientIP, int clientPort) throws CommunicationException {
        // Attempt to create new connection to host IP
        try {
            return new Socket(clientIP, clientPort);
        } catch (IOException e) {
            throw new CommunicationException("Could not set up client socket", e);
        }
    }

    @Override
    public void send(Sendable sendable, Socket client) throws CommunicationException {
        // Check that the client socket is open
        if (client == null || client.isClosed()) {
            throw new CommunicationException("Client socket not opent to write to!");
        }

        // Convert object to JSON
        String msg = gson.toJson(sendable, sendable.getClass());

        try {
            byte buffer[] = msg.getBytes();
            client.getOutputStream().write(buffer);
        } catch (IOException e) {
            throw new CommunicationException("Could not write to client buffer", e);
        }
    }

    @Override
    public void disconnectFromClient(Socket clientSocket) throws CommunicationException {
        // Try to close the socket
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new CommunicationException("Could not close client connection", e);
        }
    }

    public Socket getDataBaseRequest() {
        return dataBaseRequest;
    }

    public void setDataBaseRequest(Socket dataBaseRequest) {
        this.dataBaseRequest = dataBaseRequest;
    }

    public Socket getDataBaseReceive() {
        return dataBaseReceive;
    }

    public void setDataBaseReceive(Socket dataBaseReceive) {
        this.dataBaseReceive = dataBaseReceive;
    }
}

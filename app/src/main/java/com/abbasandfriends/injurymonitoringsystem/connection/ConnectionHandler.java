package com.abbasandfriends.injurymonitoringsystem.connection;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.Consumer;
import controller.Producer;
import exception.CommunicationException;
import sendable.Sendable;
import sendable.alarm.Alarm;

public class ConnectionHandler implements Consumer, Producer {

    private Socket server;
    private Socket dataBaseReceive;
    private Socket dataBaseRequest;
    private Gson gson;

    public ConnectionHandler() {
        gson = new Gson();
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
        try {
            // Get the input stream from the socket and read it into a string
            InputStream inputStream = clientSocket.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

            if(!scanner.hasNext()) {
                return null;
            }
            String msg = scanner.next();

            // Create readers to separate objects
            JsonReader jsonReader = new JsonReader(new StringReader(msg));
            jsonReader.setLenient(true);

            // Add objects to list
            List<Sendable> received = new ArrayList<Sendable>();
            while(jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                Alarm alarm = gson.fromJson(jsonReader, Alarm.class);
                received.add(alarm);
            }

            return received;

        } catch (IOException e) {
            throw new CommunicationException("Could not read from input buffer", e);
        }
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

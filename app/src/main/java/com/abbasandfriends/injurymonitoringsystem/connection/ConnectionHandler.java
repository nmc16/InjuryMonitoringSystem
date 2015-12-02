package com.abbasandfriends.injurymonitoringsystem.connection;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import controller.Consumer;
import controller.Producer;
import exception.CommunicationException;
import sendable.Sendable;

public class ConnectionHandler implements Consumer, Producer {

    private ServerSocket server;
    private Socket dataBaseConnection;

    @Override
    public void host(int hostPort, InetAddress ip) throws CommunicationException {
        
    }

    @Override
    public Socket acceptClient() throws CommunicationException {
        return null;
    }

    @Override
    public List<Sendable> receive(Socket clientSocket) throws CommunicationException {
        return null;
    }

    @Override
    public void disconnectHost() throws CommunicationException {

    }

    @Override
    public Socket connectTo(String clientIP, int clientPort) throws CommunicationException {
        return null;
    }

    @Override
    public void send(Sendable sendable, Socket client) throws CommunicationException {

    }

    @Override
    public void disconnectFromClient(Socket clientSocket) throws CommunicationException {

    }
}

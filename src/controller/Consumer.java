package controller;

import exception.CommunicationException;
import sendable.Sendable;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;

/**
 * Object that can produce objects to send over TCP sockets to a host
 * machine using JSON.
 *
 * @version 1
 */
public interface Consumer {
    /**
     * Starts a host connection on localhost using a {@link java.net.ServerSocket}.
     *
     * Starts accepting connections and waits until connection is found.
     *
     * Sets up the input stream to write into the socket.
     *
     * @param hostPort Port number to connect the host on
     * @throws CommunicationException Thrown if could not connect server socket to host port
     */
	void host(int hostPort, InetAddress ip) throws CommunicationException;

    /**
     * Receives all classes that exist on the buffer and attempts to parse the JSON
     * strings into the class type passed.
     *
     * @param inputStream Input stream object from the client socket to read from
     * @return Returns a list containing the objects on the buffer
     * @throws CommunicationException Thrown if could not read the input buffer
     */
    List<Sendable> receive(InputStream inputStream) throws CommunicationException;

    /**
     * Attempts to disconnect the host server socket and close the input stream attached
     * to it.
     *
     * @throws CommunicationException Thrown if could not close input stream or server socket
     */
	void disconnectHost() throws CommunicationException;
}

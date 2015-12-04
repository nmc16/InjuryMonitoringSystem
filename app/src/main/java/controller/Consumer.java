package controller;

import exception.CommunicationException;
import sendable.Sendable;

import java.net.InetAddress;
import java.net.Socket;
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
     * @param hostPort Port number to connect the host on
     * @param ip {@link InetAddress} of the IP to bind the host to
     * @throws CommunicationException Thrown if could not connect server socket to host port
     */
	void host(int hostPort, InetAddress ip) throws CommunicationException;

	/**
	 * Accepts a client trying to connect to the host machine. 
	 * 
	 * Sets up the socket for the client connection and returns it.
	 * 
	 * @return Client socket attempting to connect to the host
	 * @throws CommunicationException Thrown if the error connecting the client socket
	 */
	Socket acceptClient() throws CommunicationException;
	
    /**
     * Receives all classes that exist on the buffer and attempts to parse the JSON
     * strings into the class type passed.
     *
     * @return Returns a list containing the objects on the buffer
     * @throws CommunicationException Thrown if could not read the input buffer
     */
    List<Sendable> receive(Socket clientSocket) throws CommunicationException;

    /**
     * Attempts to disconnect the host server socket and close the input stream attached
     * to it.
     *
     * @throws CommunicationException Thrown if could not close input stream or server socket
     */
	void disconnectHost() throws CommunicationException;
}

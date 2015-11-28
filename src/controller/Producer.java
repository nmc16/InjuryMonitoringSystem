package controller;

import exception.CommunicationException;
import sendable.Sendable;

/**
 * Object that can produce objects to send over TCP sockets to a host
 * machine using JSON.
 *
 * @version 1
 */
public interface Producer {

    /**
     * Attempts to create a TCP socket connection between the object and the
     * client given the IP Address and the port.
     *
     * Sets up the socket to use for sending the data. Must be called before
     * attempting to send any data.
     *
     * @param clientIP String representing the IP address to connect to
     * @param clientPort Client port to connect socket to
     * @throws CommunicationException Thrown on error connecting socket
     */
	void connectTo(String clientIP, int clientPort) throws CommunicationException;

    /**
     * Sends a {@link Sendable} object using JSON over the client TCP socket created
     * from {@link #connectTo(String, int)}.
     *
     * @param sendable Sendable object to send over the socket
     * @throws CommunicationException Thrown if error writing to socket or if socket not open
     */
	void send(Sendable sendable) throws CommunicationException;

    /**
     * Disconnects the socket from the client connection and closes the output stream
     * that was being written to.
     *
     * @throws CommunicationException Thrown if there is an error closing the socket or output stream
     */
	void disconnectFromClient() throws CommunicationException;
}
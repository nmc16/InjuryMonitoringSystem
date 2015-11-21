package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exception.CommunicationException;
import sendable.Sendable;
import sendable.data.Acceleration;
import exception.ThresholdException;
import sendable.data.Position;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.Gson;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

/**
 * Database controller to handle the calculation of sendable.
 *
 * @version 1
 */
public class Controller implements Producer, Consumer {

	private static final int BUFFER_SIZE = 65536;
	private Gson gson = new Gson();
    private InputStream inputStream;
    private OutputStream outputStream;
    private double threshold;
    private ServerSocket server;
    private Socket clientSocket;
    private Socket inputSocket;
    
    public Controller(double threshold) {
        this.threshold = threshold;
    }

    public Acceleration calculate(Position p1, Position p2) throws ThresholdException {
        // Calculate the acceleration sendable
        int deltaX = p2.getxPos() - p1.getxPos();
        int deltaY = p2.getyPos() - p1.getyPos();
        int deltaZ = p2.getzPos() - p1.getzPos();

        int xAccel = (int) (deltaX / ((p2.getTime() - p1.getTime()) / 1000));
        int yAccel = (int) (deltaY / ((p2.getTime() - p1.getTime()) / 1000));
        int zAccel = (int) (deltaZ / ((p2.getTime() - p1.getTime()) / 1000));

        double accel = sqrt(pow(deltaX, 2) + pow(deltaY, 2) + pow(deltaZ, 2));

        if (accel >= threshold) {
            throw new ThresholdException("Threshold value exceeded: " + accel);
        }

        // Create new DB Data object with new acceleration sendable and return
        return new Acceleration(p2.getUID(), p2.getTime(), xAccel, yAccel, zAccel, accel);
    }

    /**
     * @see Consumer#receive(Class)
     */
	@Override
	public <T extends Sendable> List<T> receive(Class<T> sendable) throws CommunicationException {
		try {
            // Write the bytes on the socket buffer until the EOF is reached
			byte buffer[] = new byte[BUFFER_SIZE];
			while (true) {
                if (inputStream.read(buffer) != -1) {
                    break;
                }
            }

            // Remove all the NUL characters from the string
			String msg = new String(buffer);
            msg = msg.replace("\u0000", "").replace("\\u0000", "");

            // Create json reader to read the seperate objects from the string
            JsonReader jsonReader = new JsonReader(new StringReader(msg));
            jsonReader.setLenient(true);

            // Add the objects to the list until the end document token is received
            List<T> received = new ArrayList<T>();
            while (jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                T t = gson.fromJson(jsonReader, sendable);
                received.add(t);
            }

            return received;
			
		} catch (IOException e) {
			throw new CommunicationException("Could not read from input buffer", e);
		}
		
	}

    /**
     * @see Producer#send(Sendable)
     */
    @Override
	public void send(Sendable sendable) throws CommunicationException {
        // Check that there is a client to write to
        if (clientSocket == null || clientSocket.isClosed()) {
            throw new CommunicationException("Client socket not open to write to! Check that you have called" +
                                             "connectTo() method.");
        }

        // Convert the object to its JSON representation
		String msg = gson.toJson(sendable, sendable.getClass());
		try {
            // Convert to byte array and write to the socket
			byte buffer[] = msg.getBytes();
			outputStream.write(buffer);

		} catch (IOException e) {
			throw new CommunicationException("Could not write to client output buffer", e);
		}
	}

    /**
     * @see Consumer#host(int)
     */
	@Override
	public void host(int hostPort) throws CommunicationException {
		// Check that the socket is not already open
		if (server != null && !server.isClosed()) {
			throw new CommunicationException("Socket was not closed before trying to host!");
		}
		
		// Start the server and create the input socket
		try {
			server = new ServerSocket(hostPort);
			inputSocket = server.accept();
            inputStream = inputSocket.getInputStream();

		} catch (IOException e) {
			throw new CommunicationException("Could not open server socket or input stream", e);
		}
	}

    /**
     * @see Consumer#disconnectHost()
     */
	@Override
	public void disconnectHost() throws CommunicationException {
        try {
            // Close the input stream and the socket
            inputStream.close();

            // If the socket is still open close it
            if (!inputSocket.isClosed()) {
                inputSocket.close();
            }

            // Close server socket
            server.close();

        } catch (IOException e) {
            throw new CommunicationException("Could not close server socket", e);
        }
	}

    /**
     * @see Producer#connectTo(String, int)
     */
	@Override
	public void connectTo(String clientIP, int clientPort) throws CommunicationException {
        // Check that the socket is not already open
		if (clientSocket != null && !clientSocket.isClosed()) {
			throw new CommunicationException("Client socket not closed before operation!");
		}
		
		try {
            // Open the client and save the output stream to write to
			clientSocket = new Socket(clientIP, clientPort);
            outputStream = clientSocket.getOutputStream();

		} catch (IOException e) {
			throw new CommunicationException("Could not set up client socket", e);
		}
	}

    /**
     * @see Producer#disconnectFromClient()
     */
	@Override
	public void disconnectFromClient() throws CommunicationException {
		try {
            // Close the output stream, should also close socket
            outputStream.close();

            // If the socket was not closed then close it
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }

        } catch (IOException e) {
            throw new CommunicationException("Could not close the client connection", e);
        }
	}
}

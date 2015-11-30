package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import exception.CommunicationException;
import sendable.Sendable;
import sendable.data.Acceleration;
import exception.ThresholdException;
import sendable.data.Position;
import json.SendableDeserializer;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

/**
 * Database controller to handle the calculation of sendable.
 *
 * @version 1
 */
public class Controller implements Producer, Consumer {

    private static final int BUFFER_SIZE = 65536;
    private Gson gson;
    private double threshold;
    private ServerSocket server;
    
    public Controller(double threshold) {
        this.threshold = threshold;
        gson = new GsonBuilder().registerTypeAdapter(Sendable.class, new SendableDeserializer()).create();
    }

    /**
     * Calculates the acceleration given two positions from the accelerometer.
     *
     * The result is an {@link Acceleration} class that holds the magnitudes
     * of all the component accelerations and the total magnitude.
     *
     * @param p1 Starting position from the sensor
     * @param p2 Final position of the senser
     * @return Acceleration object holding the magnitudes of the components
     * @throws ThresholdException Thrown if calculation crosses the threshold defined in constructor
     */
    public Acceleration calculate(Position p1, Position p2) throws ThresholdException {
        // Calculate the acceleration sendable
        int deltaX = p2.getxPos() - p1.getxPos();
        int deltaY = p2.getyPos() - p1.getyPos();
        int deltaZ = p2.getzPos() - p1.getzPos();

        int xAccel = (int) (deltaX / ((p2.getTime() - p1.getTime()) / 1000.0));
        int yAccel = (int) (deltaY / ((p2.getTime() - p1.getTime()) / 1000.0));
        int zAccel = (int) (deltaZ / ((p2.getTime() - p1.getTime()) / 1000.0));

        double accel = sqrt(pow(deltaX, 2) + pow(deltaY, 2) + pow(deltaZ, 2));

        if (accel >= threshold) {
            throw new ThresholdException("Threshold value exceeded: " + accel,
                                         new Acceleration(p2.getUID(), p2.getTime(), xAccel, yAccel, zAccel, accel));
        }

        // Create new DB Data object with new acceleration sendable and return
        return new Acceleration(p2.getUID(), p2.getTime(), xAccel, yAccel, zAccel, accel);
    }

    /**
     * @see Consumer#receive(InputStream)
     */
	@Override
	public List<Sendable> receive(Socket clientSocket) throws CommunicationException {
		try {
			// Get the input stream from the socket
			InputStream inputStream = clientSocket.getInputStream();
			
            // Write the bytes on the socket buffer until the EOF is reached
			byte buffer[] = new byte[BUFFER_SIZE];
			while (true) {
                if (inputStream.read(buffer) != -1) {
                    break;
                }
            }

            // Remove all the NULL characters from the string
            String msg = new String(buffer);
            msg = msg.replace("\u0000", "").replace("\\u0000", "");

            // Create JSON reader to read the separate objects from the string
            JsonReader jsonReader = new JsonReader(new StringReader(msg));
            jsonReader.setLenient(true);

            // Add the objects to the list until the end document token is received
            List<Sendable> received = new ArrayList<Sendable>();
            while (jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                Sendable parsedObj = gson.fromJson(jsonReader, Sendable.class);
                received.add(parsedObj);
            }

            return received;
			
		} catch (IOException e) {
			throw new CommunicationException("Could not read from input buffer", e);
		}
		
	}

    /**
     * @see Producer#send(Sendable, Socket)
     */
    @Override
	public void send(Sendable sendable, Socket client) throws CommunicationException {
        // Check that there is a client to write to
        if (client == null || client.isClosed()) {
            throw new CommunicationException("Client socket not open to write to! Check that you have called" +
                                             "connectTo() method.");
        }

        // Convert the object to its JSON representation
		String msg = gson.toJson(sendable, sendable.getClass());
		try {
            // Convert to byte array and write to the socket
			byte buffer[] = msg.getBytes();
			client.getOutputStream().write(buffer);

        } catch (IOException e) {
            throw new CommunicationException("Could not write to client output buffer", e);
		}
	}

    /**
     * @see Consumer#host(int, InetAddress)
     */
	@Override
	public void host(int hostPort, InetAddress ip) throws CommunicationException {
		// Check that the socket is not already open
		if (server != null && !server.isClosed()) {
			throw new CommunicationException("Socket was not closed before trying to host!");
		}
		
		// Start the server and create the input socket
		try {
			server = new ServerSocket(hostPort, 50, ip);

		} catch (IOException e) {
			throw new CommunicationException("Could not open server socket or input stream", e);
		}
	}

	/**
	 * @see Consumer#acceptClient()
	 */
	@Override
    public Socket acceptClient() throws CommunicationException {
        // Check that the socket is not already open
        if (server == null || server.isClosed()) {
            throw new CommunicationException("Host socket is closed!");
        }

        // Start the server and create the input socket
        try {
            return server.accept();
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
	public Socket connectTo(String clientIP, int clientPort) throws CommunicationException {
		
		try {
            // Open the client and save the output stream to write to
			return new Socket(clientIP, clientPort);

		} catch (IOException e) {
			throw new CommunicationException("Could not set up client socket", e);
		}
	}

    /**
     * @see Producer#disconnectFromClient(Socket)
     */
	@Override
	public void disconnectFromClient(Socket clientSocket) throws CommunicationException {
		try {
            // Close the output stream, should also close socket
            clientSocket.getOutputStream().close();

            // If the socket was not closed then close it
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }

        } catch (IOException e) {
            throw new CommunicationException("Could not close the client connection", e);
        }
	}
}

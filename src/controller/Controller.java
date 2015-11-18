package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import sendable.Sendable;
import sendable.data.Acceleration;
import exception.ThresholdException;
import sendable.data.Position;

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
    private double threshold;
    ServerSocket server;
    Socket clientSocket;
    Socket inputSocket;
    
    public Controller(double threshold) {
        this.threshold = threshold;
    }

    public Acceleration calculate(Position p1, Position p2) throws ThresholdException {
        // Calculate the acceleration sendable
        int deltaX = p2.getxPos() - p1.getxPos();
        int deltaY = p2.getyPos() - p1.getyPos();
        int deltaZ = p2.getzPos() - p1.getzPos();

        int xAccel = (int) (deltaX / ((p2.getTime().getTime() - p1.getTime().getTime()) / 1000));
        int yAccel = (int) (deltaY / ((p2.getTime().getTime() - p1.getTime().getTime()) / 1000));
        int zAccel = (int) (deltaZ / ((p2.getTime().getTime() - p1.getTime().getTime()) / 1000));

        double accel = sqrt(pow(deltaX, 2) + pow(deltaY, 2) + pow(deltaZ, 2));

        if (accel >= threshold) {
            throw new ThresholdException("Threshold value exceeded: " + accel);
        }

        // Create new DB Data object with new acceleration sendable and return
        return new Acceleration(p2.getUID(), p2.getTime(), xAccel, yAccel, zAccel, accel);
    }
    
    

	@Override
	public <T extends Sendable> T receive(Class<T> sendable) {
		try {
			InputStream inputStream = inputSocket.getInputStream();
			byte buffer[] = new byte[BUFFER_SIZE];
			while (inputStream.read(buffer) != -1) {
			}
			
			String msg = new String(buffer);
			T object = gson.fromJson(msg, sendable);
			
			inputStream.close();
			return object;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public void send(Sendable sendable, Class<?> sendableType) {
		String msg = gson.toJson(sendable, sendableType);
		
		try {
			OutputStream outputStream = clientSocket.getOutputStream();
			
			byte buffer[] = new byte[BUFFER_SIZE];
			buffer = msg.getBytes();
			
			outputStream.write(buffer);
			outputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void host(int hostPort) {
		// Check that the socket is not already open
		if (server != null || !server.isClosed()) {
			// TODO change this to throw checked exception
			throw new RuntimeException("Socket was not closed before trying to host!");
		}
		
		// Start the server and create the input socket
		try {
			server = new ServerSocket(hostPort);
			inputSocket = server.accept();
		} catch (IOException e) {
			// TODO throw new exception
			e.printStackTrace();
			return;
		}
		
	}

	@Override
	public void disconnectHost() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectTo(String clientIP, int clientPort) {
		if (clientSocket != null || !clientSocket.isClosed()) {
			throw new RuntimeException("Socket was not closed before connecting to client!");
		}
		
		try {
			clientSocket = new Socket(clientIP, clientPort);
		} catch (IOException e) {
			// TODO throw new exception
			e.printStackTrace();
			return;
		}
		
		
	}

	@Override
	public void disconnectFromClient() {
		// TODO Auto-generated method stub
		
	}
    
	public static void main (String args[]) {
		
	}
    
}

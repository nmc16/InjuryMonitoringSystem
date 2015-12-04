package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import sendable.Sendable;
import json.SendableDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.pi4j.component.light.LED;
import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceLed;
import com.pi4j.device.piface.PiFaceSwitch;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.wiringpi.Spi;

import exception.CommunicationException;

/**
 * Object that implements the LED lights to turn on and blink when
 * receiving a signal from the database.
 * 
 * Also implents a method to turn off the LED lights when the signal
 * is received.
 *
 * @version 1
 */
public class EmergencySystem implements Consumer {
	// creates global variables
	private static final Logger LOG = Logger.getLogger("ESLoger");
	private PiFace piFace;
	private ArrayList<PiFaceLed> ledList = new ArrayList<PiFaceLed>();
	private final int LED_ON = 500;
	private final int LED_OFF = 0;
	private final int BUFFER_SIZE = 65536;
	private final int MAX_CONNECTION = 50;
	private Gson gson = new GsonBuilder().registerTypeAdapter(Sendable.class, new SendableDeserializer()).create();
	private ServerSocket server;
	private String hostIP;
	private int hostPort;
	private int index = 0;
	private Socket hostSocket;
	
	// Constructor to initialize variable and create new object of the class
	public EmergencySystem() throws IOException {
		piFace = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
		ledList = new ArrayList<PiFaceLed>();
		
		// Add the off LEDs to the off list
		ledList.add(PiFaceLed.LED0);
		ledList.add(PiFaceLed.LED1);
		ledList.add(PiFaceLed.LED2);
		ledList.add(PiFaceLed.LED3);
		ledList.add(PiFaceLed.LED4);
		ledList.add(PiFaceLed.LED5);
		ledList.add(PiFaceLed.LED6);
		ledList.add(PiFaceLed.LED7);
		
		// Reset all the LEDs in that list to turn off
		for (PiFaceLed led : ledList) {
			piFace.getLed(led).off();
		}
	}
	
	// Getters
	public String getHostIP(){
		return hostIP;	
	}
	
	public int getHostPort(){
		return hostPort;	
	}
	
	// Setters 
	public void setHostIP(String IP){
		this.hostIP= IP;
	}
	
	public void setHostPort(int clientPort){
		this.hostPort=clientPort;
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
			server = new ServerSocket(hostPort, MAX_CONNECTION, ip);
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
     * method that handles when a signal is being received and allows the
     * lights to turn on
     *
     * When the button is clicked, it turns off the light(s)
     */
	public void handleEmergency(Sendable sendable) {
		if (index > ledList.size() - 1) {
			return;
		}
		
		// Get led from the off list and add it to the on list
		final PiFaceLed led = ledList.get(index);
		piFace.getLed(led).blink(LED_ON);
	    index++;
	
		piFace.getSwitch(PiFaceSwitch.S1).addListener(new SwitchListener() {
			@Override
			// the lights are changed to off with blink and off
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					piFace.getLed(led).blink(LED_OFF);
					piFace.getLed(led).off();
					index = 0;
				}
			}
		});
	}
	
	public static void main(String args[]) throws InterruptedException, IOException, CommunicationException {
		// initialize variables used in main
		InetAddress ip;
		
		EmergencySystem emergency = new EmergencySystem();
		Scanner in = new Scanner(System.in);

		// Take in the location of the database/controller 
		LOG.info("Please enter the IP of the controller 10.0.0.X:"); 

		emergency.setHostIP("10.0.0."+ in.next());
		LOG.info("The IP of the controller is: " + emergency.getHostIP());
	
		// Take in the port of the client 
		LOG.info("Please enter the port number:");

		emergency.setHostPort(in.nextInt());
		LOG.info("The port number is: " + emergency.getHostPort());
		
		ip = InetAddress.getByName(emergency.getHostIP());
		
		// Use the connect to method to set the socket 
		emergency.host(emergency.getHostPort(),ip);

		while(true) {
				// socket is created and initialized 
				Socket socket = emergency.acceptClient();	
				LOG.info("Device connected!");

				(new Thread(new EmergencyRunner(socket, emergency))).start();
		}
	}
}	

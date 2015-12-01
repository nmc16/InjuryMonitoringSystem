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

import sendable.Sendable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceLed;
import com.pi4j.device.piface.PiFaceSwitch;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.wiringpi.Spi;

import exception.CommunicationException;

public class EmergencySystem implements Consumer {
	// creates global variables
	private PiFace piFace;
	
	
	private final int BUFFER_SIZE = 65536;
	private Gson gson;
	private ServerSocket server;
	private String hostIP;
	private int hostPort;
	private Socket hostSocket;
	
	
	// getters 
	public String getHostIP(){
		return hostIP;	
	}
	public int getHostPort(){
		return hostPort;	
	}
	public Socket getHostSocket(){
		return hostSocket;	
	}
	
	
	// setters 
	
	public void setHostIP(String IP){
		this.hostIP= IP;
	}
	
	
	public void setHostPort(int clientPort){
		this.hostPort=clientPort;
	}
	public void setHostSocket(Socket clientSocket){	
		this.hostSocket=clientSocket;
	}
	
	// constructor to intialize variable
	public EmergencySystem() throws IOException {
		piFace = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
		
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

	// button handler to initialize the lights to start blinking and when button is pressed turn off
	public void setUpButtonHandler() {
		piFace.getLed(PiFaceLed.LED0).blink(500);
		piFace.getLed(PiFaceLed.LED1).blink(500);
		piFace.getLed(PiFaceLed.LED2).blink(500);
		piFace.getLed(PiFaceLed.LED3).blink(500);
		piFace.getLed(PiFaceLed.LED4).blink(500);
		piFace.getLed(PiFaceLed.LED5).blink(500);
		piFace.getLed(PiFaceLed.LED6).blink(500);
		piFace.getLed(PiFaceLed.LED7).blink(500);
		// button listener to add the buttons and imply the switch is clicked
		piFace.getSwitch(PiFaceSwitch.S1).addListener(new SwitchListener() {
			@Override
			// the lights are changed to off with blink and off
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					piFace.getLed(PiFaceLed.LED0).blink(0);
					piFace.getLed(PiFaceLed.LED0).off();
					piFace.getLed(PiFaceLed.LED1).blink(0);
					piFace.getLed(PiFaceLed.LED1).off();
					piFace.getLed(PiFaceLed.LED2).blink(0);
					piFace.getLed(PiFaceLed.LED2).off();
					piFace.getLed(PiFaceLed.LED3).blink(0);
					piFace.getLed(PiFaceLed.LED3).off();
					piFace.getLed(PiFaceLed.LED4).blink(0);
					piFace.getLed(PiFaceLed.LED4).off();
					piFace.getLed(PiFaceLed.LED5).blink(0);
					piFace.getLed(PiFaceLed.LED5).off();
					piFace.getLed(PiFaceLed.LED6).blink(0);
					piFace.getLed(PiFaceLed.LED6).off();
					piFace.getLed(PiFaceLed.LED7).blink(0);
					piFace.getLed(PiFaceLed.LED7).off();
				}
			}
		});	
	}
	
	
	
	
	
	// main method to run the code
	public static void main(String args[]) throws InterruptedException, IOException, CommunicationException {
		List<Sendable> emergencies;

		InetAddress ip;
		
		
		EmergencySystem emergency = new EmergencySystem();
		
		
		
		Scanner in = new Scanner(System.in);
		// Take in the location of the database / controller 
		System.out.println("\n please enter the IP of the Data base 10.0.0.X "); 
		emergency.setHostIP("10.0.0."+ in.next());
		System.out.println("\n The IP of the client is:" +emergency.getHostIP());
	
		//Take in the port of the client 
		System.out.println("\n please enter the clients port number ");
		emergency.setHostPort(in.nextInt());
		System.out.println("The clients port number is: " + emergency.getHostPort());

		
	
		
		ip = InetAddress.getByName(emergency.getHostIP());
		//use the connect to method to set the socket 
		emergency.host(emergency.getHostPort(),ip);
		emergency.setHostSocket(emergency.acceptClient());
		boolean waiting = true;
		while(waiting){
			
				emergencies=emergency.receive(emergency.getHostSocket());
				if(emergencies != null){
					emergency.setUpButtonHandler();	
					System.out.println("Would you like to continue listening for alarms ?  [T][F]");
					if( in.next()== "F"){
						waiting = false;
					}
				}
			
		}
		
		emergency.disconnectHost();
		in.close();
	}


}
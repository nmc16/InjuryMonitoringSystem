import java.io.IOException;
import java.net.Socket;




import controller.Producer;
import exception.CommunicationException;
import sendable.Sendable;
import sendable.data.Position;

import com.google.gson.Gson;
import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1015Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.i2c.I2CBus;

import java.util.Scanner;



/**
 * Player to send information to the controller/ data base 
 * @author charliehardwickkelly
 * 
 */
public class player implements Producer {
	
	// holder variables for sensing function
	private String clientIP; 
	private int clientPort; 
	
	// here holder variables initialized 
	private double playerID; 
	private double xcoord;
	private double ycoord;
	private double zcoord; 

	
	
	private static int uid=1; 
	
	// These variables are to send to the controller 
	private Socket clientSocket;
	private Gson gson = new Gson();	
	
	
	
	
	
	public player(int playerID){
		this.playerID=playerID; 
	}
	
	
	//getters
	
	public Socket getClientSocket(){
		return clientSocket;	
	}
	public double getPlayerID(){
		return playerID;
	}
	public double getXcoord(){
		return xcoord;
	}
	public double getYcoord(){
		return ycoord;
	}
	public double getZcoord(){
		return zcoord;
	}
	
	public String getClientIP(){
		return clientIP;
		
	}
	
	public int getClientPort(){
		return clientPort;	
	}
	
	//setters 
	
	public void setPlayerID(double playerID){
		this.playerID = playerID;
		
	}
	public void setXcoord(double xcoord){
		this.xcoord=xcoord;
	}
	
	public void setYcoord(double ycoord){
		this.ycoord=ycoord;
	}
	public void setxZcoord(double zcoord){
		this.zcoord=zcoord;
	}

	public void setClientIP(String IP){
		
		this.clientIP= IP;
		
	}
	public void setClientPort(int clientPort){
		
		this.clientPort=clientPort;
	}
	public void setClientSocket(Socket clientSocket){	
		this.clientSocket=clientSocket;
	}
	
	public void setup(player p) throws CommunicationException{
		
		Scanner in = new Scanner(System.in);
		// Take in the the player UID number 
		System.out.println("please enter Player UID");
		p.setPlayerID(in.nextInt());
		
		System.out.println("the Players ID is: "+ p.getPlayerID());
		
		// Take in the location of the database / controller 
		System.out.println("\n please enter the IP of the Data base 10.0.0.X "); 
		p.setClientIP("10.0.0."+ in.next());
		System.out.println("\n The IP of the client is:" +p.getClientIP());
		
		System.out.println("\n please enter the clients port number ");
		p.setClientPort(in.nextInt());
		System.out.println("The clients port number is: " + p.getClientPort());
		
		//use connect to 
		 p.setClientSocket(connectTo(clientIP,clientPort));
		
		 in.close();
		
		
	}
	 
	public static void shutdownGPIO(){
		
	
		
	}
	
	
	

	
	
	
	// nics code for sending data between devices 
	
	

    
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

    public static void main(String args[]) throws InterruptedException, IOException, CommunicationException {	
		
		GpioController gpio = GpioFactory.getInstance();
		player player = new player(1); 
		
		player.setup(player);
	
		
		
		System.out.println("starting the accelleromenter");

		// create custom ADS1015 GPIO provider
	    final ADS1015GpioProvider gpioProvider = new ADS1015GpioProvider(I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x48);
	     
	     
	    //  gpio analog input pins from ADS1015
	    GpioPinAnalogInput myInputs[] = {
	    		gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A0, "Z"),
	    		gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A1, "Y"),
	    		gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A2, "X"),
	    		gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A3, "Extra"),
	    };
	     
	    // set the gain amplifier on the Anolog to digital converter
	    gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_4_096V, ADS1015Pin.ALL);
	     
	    // Define a threshold value for each pin for analog value change events to be raised.
	    // It is important to set this threshold high enough so that you don't overwhelm your program with change events for insignificant changes
	    gpioProvider.setEventThreshold(150, ADS1015Pin.ALL);
	     
	    // Define the monitoring thread refresh interval (in milliseconds).
	    // This governs the rate at which the monitoring thread will read input values from the ADC chip
	    // (a value less than 50 ms is not permitted)
	    gpioProvider.setMonitorInterval(050);
	     
	     
	     
	     // create analog pin value change listener
	    GpioPinListenerAnalog listener = new GpioPinListenerAnalog()
	    {
	    	@Override
	    	public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event)
	    	{
	    		// RAW value
	    		double value = event.getValue();
	    		
	    		// percentage
	    		double percent =  ((value * 100) / ADS1015GpioProvider.ADS1015_RANGE_MAX_VALUE);
	    		 
	                
	    		// approximate voltage ( *scaled based on PGA setting )
	    		double voltage = gpioProvider.getProgrammableGainAmplifier(event.getPin()).getVoltage() * (percent/100);

	    		// display output
	    		
	    		//System.out.print("\r (" + event.getPin().getName() +") : VOLTS=" + df.format(voltage) + "  | PERCENT=" + pdf.format(percent) + "% | RAW=" + value + "       ");
	    		
	    		if(event.getPin().getName() == "X"){
	    			player.setXcoord(voltage); 
	    			
	    			 
	    		}
	    		if(event.getPin().getName() == "Y"){
	    			 player.setYcoord(voltage);
	    			
	    			 
	    		}
	    		if(event.getPin().getName() == "Z"){
	    			 player.setxZcoord(voltage);
	    			
	    			 
	    		}  
	    		 
	    		
	    		
	    		// Create a position variable to send 
	    		
	    		System.out.println("Made it past print");
	    		Position position = new Position(uid,System.currentTimeMillis(),(int)player.getXcoord(),(int)player.getYcoord(),(int)player.getZcoord());
	    		// Attempt to send the position 
	    		try {
					player.send(position,player.getClientSocket());
				} catch (CommunicationException e) {
					System.out.println("Faild to send position");
				}
	    		System.out.println("Position sent");
	    		
	    				
	    		System.out.println(" the X value is " + player.getXcoord());
	    		System.out.println(" the Y value is " + player.getYcoord());
	    		System.out.println(" the Z value is " + player.getZcoord());
	    	 
	    	}

			
	    };
	     
	    myInputs[0].addListener(listener);
	    myInputs[1].addListener(listener);
	    myInputs[2].addListener(listener);
	    myInputs[3].addListener(listener);
	    
	   
        // keep program running for 10 minutes 
        for (int count = 0; count < 600; count++) {

            // display output
            Thread.sleep(1000);
        }
	    
	   
	        
		gpio.shutdown();
        System.out.print("");
        System.out.print("");
		
		
	}
	
	
	
}
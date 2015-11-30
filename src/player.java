import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
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
	// here holder variables initialized 
	private static double playerID; 
	private static double Xcoord;
	private static double Ycoord;
	private static double Zcoord; 
	
	// These variables are to send to the controller 
	private ServerSocket server;
	private Socket clientSocket;
	private Socket inputSocket;
	private Gson gson = new Gson();	
	private static final int BUFFER_SIZE = 65536;
	
	private static Position position;
	
	private OutputStream outputStream;
	
	// create scanner 
	private static Scanner in = new Scanner(System.in);
	
	private static boolean playing; 
	
	// create gpio controller
	private static final GpioController gpio = GpioFactory.getInstance();
	
	
	public player(int playerID){
		player.playerID=playerID; 
	}
	
	
	public static void main(String args[]) throws InterruptedException, IOException {	
		System.out.println("please enter Player id");
		playerID = in.nextInt();
		
	
		
		
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
	    			 
	    			Xcoord = voltage;
	    			 
	    		}
	    		if(event.getPin().getName() == "Y"){
	    			 
	    			Ycoord = voltage;
	    			 
	    		}
	    		if(event.getPin().getName() == "Z"){
	    			 
	    			Zcoord = voltage;
	    			 
	    		}
	    		 
	    		
	    		
	    		
	    		
	    		System.out.println("Made it past print");
	    		//position.setxPos((int)Xcoord);
	    		//position.setyPos((int)Ycoord);
	    		//position.setzPos((int)Xcoord);
	    		
			System.out.println(" the X value is " + Xcoord);
	    		System.out.println(" the Y value is " + Ycoord);
	    		System.out.println(" the Z value is " + Zcoord);
	    	 
	    	}

			
	    };
	     
	    myInputs[0].addListener(listener);
	    myInputs[1].addListener(listener);
	    myInputs[2].addListener(listener);
	    myInputs[3].addListener(listener);
	    
	   
        // keep program running for 10 minutes 
        for (int count = 0; count < 600; count++) {

            // display output
            //System.out.print("\r ANALOG VALUE (FOR INPUT A0) : VOLTS=" + df.format(voltage) + "  | PERCENT=" + pdf.format(percent) + "% | RAW=" + value + "       ");
            Thread.sleep(1000);
        }
	    
	    shutdownGPIO();
	        
		
		
	}
	 
	public static void shutdownGPIO(){
		
		gpio.shutdown();
        System.out.print("");
        System.out.print("");
		
		
	}
	
	
	

	
	
	
	// nics code for sending data between devices 
	
	
	
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
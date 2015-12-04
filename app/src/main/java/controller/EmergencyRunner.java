package controller;

import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

import exception.CommunicationException;
import sendable.Sendable;

/**
 * Object that creates a thread that will handle the emergencies 
 * that are received.
 *  
 * @version 1
 */
public class EmergencyRunner implements Runnable {
    // global variables
	private static final Logger LOG = Logger.getLogger("ESLoger");
	private Socket socket;
	private EmergencySystem emergency;

    // constructor
	public EmergencyRunner(Socket socket, EmergencySystem emergency) {
		this.socket = socket;
		this.emergency = emergency;
	}

	@Override
	public void run() {
		while(true) { 
			try {
				List<Sendable> emergencies = emergency.receive(socket);
				
				for(Sendable sendable : emergencies) {
					emergency.handleEmergency(sendable);
				}
			} catch (CommunicationException e) {
				LOG.info("Exception occurred trying to receieve alarms: " + e.getMessage());
				System.exit(1);
			}
		}
	}
}

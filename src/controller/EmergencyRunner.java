package controller;

import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

import exception.CommunicationException;
import sendable.Sendable;

public class EmergencyRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger("ESLoger");
	private Socket socket;
	private EmergencySystem emergency;
	
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

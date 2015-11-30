package controller;
import java.io.IOException;
import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceLed;
import com.pi4j.device.piface.PiFaceRelay;
import com.pi4j.device.piface.PiFaceSwitch;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.wiringpi.Spi;

public class EmergencySystem {
	// creates global variables
	private static PiFace piFace;
	private static final int onTime =125;
	// constructor to intialize variable
	public EmergencySystem() throws IOException {
		piFace = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
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
	public static void main(String args[]) throws InterruptedException, IOException {
		EmergencySystem emergency = new EmergencySystem();
		emergency.setUpButtonHandler();
	}


}
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

public class PiSystem {
	// global variables
	private static PiFace piFace;
	private static final int onTime =125;
	// contructor initializing variable
	public PiSystem() throws IOException {
		piFace = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
	}
	// button handler that runs until button is pressed
	public void setUpButtonHandler() {
		piFace.getLed(PiFaceLed.LED0).blink(500);
		piFace.getLed(PiFaceLed.LED1).blink(500);
		piFace.getLed(PiFaceLed.LED2).blink(500);
		piFace.getLed(PiFaceLed.LED3).blink(500);
		piFace.getLed(PiFaceLed.LED4).blink(500);
		piFace.getLed(PiFaceLed.LED5).blink(500);
		piFace.getLed(PiFaceLed.LED6).blink(500);
		piFace.getLed(PiFaceLed.LED7).blink(500);
		// button that gets built inside the method
		piFace.getSwitch(PiFaceSwitch.S1).addListener(new SwitchListener() {
			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					piFace.getLed(PiFaceLed.LED0).blink(0);
					piFace.getLed(PiFaceLed.LED0).off();
					piFace.getRelay(PiFaceRelay.K0).open();
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
	// main that runs the code
	public static void main(String args[]) throws InterruptedException, IOException {
		PiSystem emergency = new PiSystem();
		emergency.setUpButtonHandler();
	}


}

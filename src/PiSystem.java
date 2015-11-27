// imports for java
import java.io.IOException;
import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.device.piface.PiFace;
import com.pi4j.device.piface.PiFaceLed;
import com.pi4j.device.piface.PiFaceSwitch;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.wiringpi.Spi;

public class PiSystem {
	private static PiFace piFace;
    // variables used to delay action for a set period of time
    private static final int onTime = 125;
    
    public PiSystem() throws IOException {
    	piFace = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
    }
    //method for third switch and its functionality
    public void setUpButtonHandler() {
        // creates listener
    	piFace.getSwitch(PiFaceSwitch.S1).addListener(new SwitchListener() {
            @Override
            // checks for if the switch is on and if it is turns on light for set amount of time
            public void onStateChange(SwitchStateChangeEvent event) {
                if(event.getNewState() == SwitchState.ON) {
                	piFace.getLed(PiFaceLed.LED2).blink(onTime);
                } else {
                	piFace.getLed(PiFaceLed.LED2).off();
                }
            }
        });
    }
    // main method to create PiFace controller and use methods
    public static void main(String args[]) throws InterruptedException, IOException {
    	PiSystem emergency = new PiSystem();
    	emergency.setUpButtonHandler();
    }
}
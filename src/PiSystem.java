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
	static int cylonSpeed = 100;
	
	public static void main(String args[]) throws InterruptedException, IOException {
		final PiFace piface = new PiFaceDevice(PiFace.DEFAULT_ADDRESS, Spi.CHANNEL_0);
		
		piface.getSwitch(PiFaceSwitch.S1).addListener(new SwitchListener() {
			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					piface.getRelay(PiFaceRelay.K0).close();
				}
				else {
					piface.getRelay(PiFaceRelay.K0).open();
				}
			}
		});
		
		piface.getSwitch(PiFaceSwitch.S2).addListener(new SwitchListener() {
			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					piface.getRelay(PiFaceRelay.K1).toggle();
				}
				else {
				}
			}
		});
		
		piface.getSwitch(PiFaceSwitch.S3).addListener(new SwitchListener() {
			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					piface.getLed(PiFaceLed.LED2).blink(125);
				}
				else {
					piface.getLed(PiFaceLed.LED2).blink(0);
					piface.getLed(PiFaceLed.LED2).off();
				}
			}
		});
		
		piface.getSwitch(PiFaceSwitch.S4).addListener(new SwitchListener() {
			@Override
			public void onStateChange(SwitchStateChangeEvent event) {
				if(event.getNewState() == SwitchState.ON) {
					cylonSpeed = 30;
				}
				else {
					cylonSpeed = 100;
				}
			}
		});
		
		while(true) {
			for (int index = PiFaceLed.LED3.getIndex(); index >= PiFaceLed.LED3.getIndex(); index--) {
				piface.getLed(index).pulse(cylonSpeed);
				Thread.sleep(cylonSpeed);
			}
		}
	}
}

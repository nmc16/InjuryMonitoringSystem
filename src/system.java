import java.util.concurrent.Callable;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;

public class system {
	public static void main(String[] args) throws InterruptedException {
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		
		GpioPinDigitalOutput myLed[] = {
			gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LED #1", PinState.LOW),
			gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "LED #2", PinState.LOW),
		};
		
		myButton.addTrigger(new GpioSetStateTrigger(PinState.HIGH, myLed[0], PinState.HIGH));
		myButton.addTrigger(new GpioSetStateTrigger(PinState.LOW, myLed[0], PinState.LOW));
		myButton.addTrigger(new GpioSyncStateTrigger(myLed[1]));
		myButton.addTrigger(new GpioPulseStateTrigger(PinState.HIGH, myLed[2], 1000));
		myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>() { 
			public Void call() throws Exception {
				return null;
			}
		}));
		
		for (;;) {
			Thread.sleep(500);
		}
	}
}

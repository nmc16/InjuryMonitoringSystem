package test;

/**
 * Test cases to test the functionality of the Database Controller.
 *
 * @see controller.Controller
 */

import controller.Controller;
import data.Position;
import data.SensorData;
import exception.ThresholdException;
import org.junit.*;

import java.util.Date;

import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ControllerTest {
    private Controller controller;
    private SensorData sensorData;
    private SensorData sensorData2;

    @Before
    public void setUp() {
        controller = new Controller(35.0);
    }

    @Test
    public void calculateTestUnderThreshold() {
        sensorData = new SensorData(new Position(0, new Date(), 10, 10, 10));
        sensorData2 = new SensorData(new Position(0, new Date(), 20, 20, 20));
        double expected = Math.sqrt(pow(sensorData2.getPosition().getxPos() - sensorData.getPosition().getxPos(), 2) +
                                    pow(sensorData2.getPosition().getyPos() - sensorData.getPosition().getyPos(), 2) +
                                    pow(sensorData2.getPosition().getzPos() - sensorData.getPosition().getzPos(), 2));

        SensorData sd = null;
        try {
            sd = controller.calculate(sensorData, sensorData2);
        } catch (ThresholdException e) {
            fail("Threshold calculation threw unexpected exception during calculation: " +
                  e.getLocalizedMessage());
        }

        assertNotNull("Sensor Database Data was never initialized!", sd);
        assertEquals("Acceleration data calculation was not equal!", expected, sd.getAcceleration().getAccelMag(), 0);
    }

    @Test
    public void calculateTestOverThreshold() {
        sensorData = new SensorData(new Position(0, new Date(), 10, 10, 10));
        sensorData2 = new SensorData(new Position(0, new Date(), 50, 50, 50));

        SensorData sd = null;
        try {
            sd = controller.calculate(sensorData, sensorData2);
        } catch (ThresholdException e) {
            return;
        }

        fail("Controller should have thrown exception!");

    }
}

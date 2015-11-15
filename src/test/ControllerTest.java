package test;

import controller.Controller;
import sendable.data.Acceleration;
import sendable.data.Position;
import exception.ThresholdException;
import org.junit.*;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases to test the functionality of the Database Controller.
 *
 * @see controller.Controller
 */
public class ControllerTest {
    private Controller controller;
    private Position position1;
    private Position position2;
    private Date d1;
    private Date d2;

    @Before
    public void setUp() {
        controller = new Controller(35.0);
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.JANUARY, 1, 1, 1, 1);
        d1 = cal.getTime();
        cal.set(2012, Calendar.JANUARY, 1, 1, 1, 2);
        d2 = cal.getTime();
    }

    @Test
    public void calculateTestUnderThreshold() {
        position1 = new Position(0, d1, 10, 10, 10);
        position2 = new Position(0, d2, 20, 20, 20);
        double ex = Math.sqrt(pow(position2.getxPos() - position1.getxPos(), 2) +
                              pow(position2.getyPos() - position1.getyPos(), 2) +
                              pow(position2.getzPos() - position1.getzPos(), 2));
        int expected = (int) ex;

        Acceleration acceleration = null;
        try {
            acceleration = controller.calculate(position1, position2);
        } catch (ThresholdException e) {
            fail("Threshold calculation threw unexpected exception during calculation: " +
                  e.getLocalizedMessage());
        }

        assertNotNull("Sensor Database Data was never initialized!", acceleration);
        assertEquals("Acceleration sendable calculation was not equal!", expected, acceleration.getAccelMag());
    }

    @Test
    public void calculateTestOverThreshold() {
        position1 = new Position(0, d1, 10, 10, 10);
        position2 = new Position(0, d2, 50, 50, 50);

        Acceleration acceleration = null;
        try {
            acceleration = controller.calculate(position1, position2);
        } catch (ThresholdException e) {
            return;
        }

        fail("Controller should have thrown exception!");

    }
}

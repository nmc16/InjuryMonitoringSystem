package test;

import controller.Controller;
import controller.ControllerRunner;
import sendable.DataType;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.PlayerCause;
import sendable.data.Acceleration;
import sendable.data.Position;
import exception.ThresholdException;
import org.junit.*;
import sendable.data.Request;
import sendable.data.Service;

import java.net.InetAddress;
import java.net.Socket;
import java.util.*;


import static java.lang.Math.pow;
import static org.junit.Assert.*;

/**
 * Test cases to test the functionality of the Database Controller.
 *
 * @see controller.Controller
 */
public class ControllerTest {
    private Controller controller;
    private Position position1;
    private Position position2;
    private long t1;
    private long t2;

    @Before
    public void setUp() {
        controller = new Controller(35.0);
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.JANUARY, 1, 1, 1, 0);

        Date d1, d2;
        d1 = cal.getTime();
        t1 = d1.getTime();

        cal.set(2012, Calendar.JANUARY, 1, 1, 1, 2);
        d2 = cal.getTime();
        t2 = d2.getTime();
    }

    @Test
    public void testCalculateUnderThreshold() {
        position1 = new Position(0, t1, 10, 10, 10);
        position2 = new Position(0, t2, 20, 20, 20);
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
    public void testCalculateOverThreshold() {
        position1 = new Position(0, t1, 10, 10, 10);
        position2 = new Position(0, t2, 50, 50, 50);

        Acceleration acceleration = null;
        try {
            acceleration = controller.calculate(position1, position2);
        } catch (ThresholdException e) {
            return;
        }

        fail("Controller should have thrown exception!");

    }

    @Test
    public void testSendAndReceive() throws Exception {

        // Run the main controller
        ControllerRunner controllerRunner = new ControllerRunner(10, 9090, InetAddress.getLocalHost());
        controllerRunner.run();
        System.out.println("Controller running");

        Random random  = new Random();
        int player = random.nextInt();

        // Connect a new controller and send test alarm
        Controller controller = new Controller(10);
        Socket connection1 = controller.connectTo(InetAddress.getLocalHost().getHostAddress(), 9090);
        Alarm alarm = new Alarm(player, System.currentTimeMillis(), new PlayerCause(player));
        controller.send(alarm, connection1);

        Controller controller2 = new Controller(10);
        Socket connection2 = controller2.connectTo(InetAddress.getLocalHost().getHostAddress(), 9090);
        long alarmtime = System.currentTimeMillis() + 500;
        Position position1 = new Position(player, System.currentTimeMillis(), 1, 2, 3);
        Position position2 = new Position(player, alarmtime, 100, 200, 300);
        controller2.send(position1, connection2);
        controller2.send(position2, connection2);
        controller2.send(new Request(player, DataType.ACCEL, -1, -1), connection2);
        controller2.send(new Request(player, DataType.POS, -1, -1), connection2);

        Thread.sleep(15000);
        List<Sendable> received = controller2.receive(connection2);
        List<Alarm> alarms = new ArrayList<Alarm>();
        List<Acceleration> accelerations = new ArrayList<Acceleration>();
        List<Position> positions = new ArrayList<Position>();

        // Distribute the sendable objects
        for (Sendable sendable : received) {
            if (sendable instanceof Alarm) {
                alarms.add((Alarm) sendable);

            } else if (sendable instanceof Service) {
               Service service = (Service) sendable;
               List<?> data = service.getData();
               for (Object o : data) {
                   if (o instanceof Acceleration) {
                       accelerations.add((Acceleration) o);
                   } else if (o instanceof Position) {
                       positions.add((Position) o);
                   }
               }
            }
        }

        controller.disconnectFromClient(connection1);
        controller2.disconnectFromClient(connection2);

        assertTrue("Alarm was not sent from controller", alarms.size() >= 1);
        assertTrue("Acceleration not received from controller", accelerations.size() >= 1);
        assertTrue("Position 1 not contained in received positions", positions.contains(position1));
        assertTrue("Position 2 not contained in received positions", positions.contains(position2));
    }
}

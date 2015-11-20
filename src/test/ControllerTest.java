package test;

import controller.Controller;
import sendable.data.Acceleration;
import sendable.data.Position;
import exception.ThresholdException;
import org.junit.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        cal.set(2012, Calendar.JANUARY, 1, 1, 1, 0);
        cal.set(Calendar.MILLISECOND, 0);
        d1 = cal.getTime();
        cal.set(2012, Calendar.JANUARY, 1, 1, 1, 2);
        d2 = cal.getTime();
    }

    @Test
    public void testCalculateUnderThreshold() {
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
    public void testCalculateOverThreshold() {
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

    @Test
    public void testSendAndReceive() throws Exception {

        ExecutorService pool = Executors.newCachedThreadPool();

        Future<List<Acceleration>> host = pool.submit(new Callable<List<Acceleration>>() {
            @Override
            public List<Acceleration> call() throws Exception {
                Controller controller = new Controller(10);
                controller.host(9090);

                Thread.sleep(1000);
                List<Acceleration> a = controller.receive(Acceleration.class);
                controller.disconnectHost();

                return a;
            }
        });

        Future<List<Acceleration>> producer = pool.submit(new Callable<List<Acceleration>>() {
            @Override
            public List<Acceleration> call() throws Exception {
                Controller controller = new Controller(10);

                Thread.sleep(500);
                controller.connectTo(InetAddress.getLocalHost().getHostAddress(), 9090);

                Acceleration a1 = new Acceleration(10, d1, 0, 0, 0, 0);
                Acceleration a2 = new Acceleration(11, d1, 0, 0, 0, 0);
                List<Acceleration> accelerations = new ArrayList<Acceleration>();
                accelerations.add(a1);
                accelerations.add(a2);

                controller.send(a1);
                controller.send(a2);
                controller.disconnectFromClient();

                return accelerations;
            }
        });

        List<Acceleration> hostList = host.get();
        List<Acceleration> producerList = producer.get();

        assertEquals("Host list not expected size", 2, hostList.size());
        assertEquals("Producer list not expected size", 2, producerList.size());
        assertEquals("First acceleration was not the same!", hostList.get(0), producerList.get(0));
        assertEquals("Second acceleration was not the same!", hostList.get(1), producerList.get(1));
    }
}

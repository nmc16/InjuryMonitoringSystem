package test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import sendable.alarm.*;
import sendable.data.Acceleration;
import sendable.data.Position;
import database.Database;
import exception.DatabaseException;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DatabaseTest {
    private static  Database database;

    @BeforeClass
    public static void init() {
        database = new Database();
        try {
            database.connect();
            database.init();
        } catch (DatabaseException e) {
            fail("Could not connect to database: " + e.getLocalizedMessage());
        }
    }

    @Test
    public void testStore() {
        // Create one of each type of data
        Acceleration acceleration = new Acceleration(10, System.currentTimeMillis(), 5, 6, 7, 8);
        Position position = new Position(10, System.currentTimeMillis(), 1, 2, 3);

        // Create one of each type of alarm
        Alarm pAlarm = new Alarm(10, System.currentTimeMillis(), new PlayerCause(10));
        Alarm tAlarm = new Alarm(10, System.currentTimeMillis(), new TrainerCause(Priority.MAJOR));
        Alarm dAlarm = new Alarm(10, System.currentTimeMillis(), new DataCause(30));

        // Attempt to store all the sendable entities, fails on runtime exception from DB
        database.store(acceleration);
        database.store(position);
        database.store(pAlarm);
        database.store(tAlarm);
        database.store(dAlarm);

        // Remove all the entries
        database.remove(acceleration);
        database.remove(position);
        database.remove(pAlarm);
        database.remove(tAlarm);
        database.remove(dAlarm);
    }

    @Test
    public void testReceiveAll() {
        // Assert there's no entries in the DB
        List<Acceleration> accelerations = database.retrieve(Acceleration.class, 10);
        assertEquals("There were already acceleration entries in the database!", 0, accelerations.size());

        // Store 3 accelerations with the same player id
        Acceleration a1 = new Acceleration(10, System.currentTimeMillis(), 5, 6, 7, 8);
        Acceleration a2 = new Acceleration(10, System.currentTimeMillis(), 15, 16, 17, 18);
        Acceleration a3 = new Acceleration(10, System.currentTimeMillis(), 25, 26, 27, 28);

        database.store(a1);
        database.store(a2);
        database.store(a3);

        // Check that retrieved objects are equal to the stored ones
        accelerations = database.retrieve(Acceleration.class, 10);
        assertEquals("Acceleration list was not expected size!", 3, accelerations.size());
        assertTrue("Acceleration 1 retrieved was not equal to created one!", accelerations.get(0).equals(a1));
        assertTrue("Acceleration 2 retrieved was not equal to created one!", accelerations.get(1).equals(a2));
        assertTrue("Acceleration 3 retrieved was not equal to created one!", accelerations.get(0).equals(a1));

        // Remove them from the DB
        database.remove(a1);
        database.remove(a2);
        database.remove(a3);
    }

    @Test
    public void testReceiveAfter() {
        // Assert there's no entries in the DB
        List<Alarm> alarms = database.retrieve(Alarm.class, 10);
        assertEquals("There were already alarm entries in the database!", 0, alarms.size());

        // Create the date to search after and a date that is before the search date
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.JANUARY, 1);
        long before = cal.getTime().getTime();
        long searchDate = new Date().getTime();

        // Store 3 alarms with the same UID
        Alarm pAlarm = new Alarm(10, before, new PlayerCause(10));
        Alarm tAlarm = new Alarm(10, before, new TrainerCause(Priority.MAJOR));
        Alarm dAlarm = new Alarm(10, System.currentTimeMillis(), new DataCause(30));

        database.store(pAlarm);
        database.store(tAlarm);
        database.store(dAlarm);

        // Check that retrieved objects are equal to the stored ones
        alarms = database.retrieve(Alarm.class, 10, searchDate);
        assertEquals("Alarm list was not expected size!", 1, alarms.size());
        assertTrue("Data alarm retrieved was not equal to created one!", alarms.get(0).equals(dAlarm));

        // Remove them from the DB
        database.remove(pAlarm);
        database.remove(tAlarm);
        database.remove(dAlarm);
    }

    @Test
    public void testReceiveBetween() {
        // Assert there's no entries in the DB
        List<Position> positions = database.retrieve(Position.class, 10);
        assertEquals("There were already position entries in the database!", 0, positions.size());

        // Create dates to search between
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.JANUARY, 1);
        long before = cal.getTime().getTime();

        cal.set(2018, Calendar.JANUARY, 1);
        long after = cal.getTime().getTime();

        long start = new Date().getTime();

        // Store 3 positions with the same UID
        Position p1 = new Position(10, before, 1, 2, 3);
        Position p2 = new Position(10, System.currentTimeMillis(), 10, 20, 30);
        Position p3 = new Position(10, after, 100, 200, 300);

        long end = new Date().getTime();

        database.store(p1);
        database.store(p2);
        database.store(p3);

        positions = database.retrieve(Position.class, 10, start, end);
        assertEquals("Position list was not expected size!", 1, positions.size());
        assertTrue("Position retrieved was not equal to created one!", positions.get(0).equals(p2));

        // Remove them from the DB
        database.remove(p1);
        database.remove(p2);
        database.remove(p3);
    }

    @Test
    public void testRemove() {
        // Create one of each type of data
        Acceleration acceleration = new Acceleration(10, System.currentTimeMillis(), 5, 6, 7, 8);
        Position position = new Position(10, System.currentTimeMillis(), 1, 2, 3);
        Alarm alarm = new Alarm(10, System.currentTimeMillis(), new PlayerCause(10));

        // Attempt to store all the sendable entities, fails on runtime exception from DB
        database.store(acceleration);
        database.store(position);
        database.store(alarm);

        // Remove the entries
        database.remove(acceleration);
        database.remove(position);
        database.remove(alarm);

        // Check the entries were removed
        List<Acceleration> accelerations = database.retrieve(Acceleration.class, 10);
        assertEquals("Accelerations were not deleted!", 0, accelerations.size());

        List<Position> positions = database.retrieve(Position.class, 10);
        assertEquals("Positions were not deleted!", 0, positions.size());

        List<Alarm> alarms = database.retrieve(Alarm.class, 10);
        assertEquals("Alarms were not deleted!", 0, alarms.size());
    }

    @AfterClass
    public static void teardown() {
        try {
            database.shutdown();
        } catch (DatabaseException e) {
            fail("Could not disconnect the database: " + e.getLocalizedMessage());
        }
    }
}

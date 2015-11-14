package test;

import data.Acceleration;
import data.Position;
import database.Database;
import exception.DatabaseException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DatabaseTest {
    private Database database;

    @Before
    public void init() {
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
        Acceleration acceleration = new Acceleration(10, new Date(), 5, 6, 7, 8);
        Position position = new Position(10, new Date(), 1, 2, 3);

        database.store(acceleration);
        database.store(position);
        // TODO add alarm store

        database.remove(acceleration);
        database.remove(position);
    }

    @Test
    public void testReceiveAll() {
        Acceleration a1 = new Acceleration(10, new Date(), 5, 6, 7, 8);
        Acceleration a2 = new Acceleration(10, new Date(), 5, 6, 7, 8);
        Acceleration a3 = new Acceleration(10, new Date(), 5, 6, 7, 8);

        database.store(a1);
        database.store(a2);
        database.store(a3);

        List<Acceleration> accelerations = database.retrieve(Acceleration.class, 10);
        assertEquals("Result list was not expected size!", 3, accelerations.size());

        // TODO check all are equal, need to implement equals method in sendable classes
    }

}

package database;

import sendable.*;
import exception.DatabaseException;
import sendable.alarm.*;
import sendable.data.Acceleration;
import sendable.data.Position;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Manages database operations for the Derby database using the JPA.
 *
 * Can create a connection to the database and create all the needed
 * tables if they don't already exist.
 *
 * Can store/delete {@link sendable.Sendable} sendable that uses the persistence
 * API annotations. Can query sendable from the tables using time, player IDs,
 * and thresholds as criteria.
 *
 * @version 1
 */
public class Database {
    private static final String driverClass = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String dbName = "sensorDB";
    private static final String connectionURL = "jdbc:derby:" + dbName + ";create=true";
    private static final String shutdownURL = "jdbc:derby:;shutdown=true";
    private static final Logger LOG = Logger.getLogger("DBLogger");
    private Connection connection = null;
    private Map<String, String> tables;
    private EntityManagerFactory factory;

    public Database() {
        // Create map that holds table creation strings
        tables = new HashMap<String, String>();
        tables.put("ALARMDATA", "CREATE TABLE ALARMDATA " +
                                "(INDEX INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                                " UID INT NOT NULL," +
                                " TIME TIMESTAMP NOT NULL, " +
                                " CAUSE_INDEX INT)");
        tables.put("CAUSE", "CREATE TABLE CAUSE " +
                            "(INDEX INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                            " MSG VARCHAR(50), " +
                            " DTYPE VARCHAR(20), " +
                            " PLAYERID INT, " +
                            " PRIORITY VARCHAR(10), " +
                            " THRESHOLD INT)");
        tables.put("ACCELDATA", "CREATE TABLE ACCELDATA " +
                                "(INDEX INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                                " PLAYERID INT NOT NULL," +
                                " TIME TIMESTAMP NOT NULL," +
                                " X_ACCEL INT NOT NULL," +
                                " Y_ACCEL INT NOT NULL," +
                                " Z_ACCEL INT NOT NULL," +
                                " ACCEL INT NOT NULL)");
        tables.put("POSDATA", "CREATE TABLE POSDATA " +
                              "(INDEX INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                              " PLAYERID INT NOT NULL," +
                              " TIME TIMESTAMP NOT NULL," +
                              " X_POS INT NOT NULL," +
                              " Y_POS INT NOT NULL," +
                              " Z_POS INT NOT NULL)");

        // Create the factory for entity manager creation
        factory = Persistence.createEntityManagerFactory("sdb");
    }

    /**
     * Creates a connection with the database if there is not one already.
     *
     * Must be called first before calling other database methods.
     *
     * @throws DatabaseException Thrown when driver class cannot be loaded or exception generated
     *                           during connection creation.
     */
    public void connect() throws DatabaseException {
        // Check if there is already a connection
        if (connection != null) {
            LOG.info("Already connected to database.");
            return;
        }

        // If there isn't a connection try and create one
        try {
            LOG.info("Connecting to database...");

            // Load the database driver
            Class.forName(driverClass);

            // Create the connection
            connection = DriverManager.getConnection(connectionURL);

            LOG.info("Connected.");
        } catch (ClassNotFoundException e) {
            LOG.severe("Could not find driver class: " + e.getLocalizedMessage());
            throw new DatabaseException("Could not find driver class: " + e.getLocalizedMessage(), e);
        } catch (SQLException e) {
            LOG.severe("Could not connect to database: " + e.getLocalizedMessage());
            throw new DatabaseException("Could not connect to database: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Initializes the tables needed for the database.
     *
     * The tables for alarms, accelerations
     * and positions are checked if they exist and creates the tables if they do not.
     *
     * @throws DatabaseException Thrown if there is an exception generated querying the DB
     */
    public void init() throws DatabaseException {
    	Statement statement;
    	try {
    		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    	} catch (SQLException e) {
    		LOG.severe("Could not create statement: " + e.getLocalizedMessage());
    		throw new DatabaseException("Could not create statement: " + e.getLocalizedMessage(), e);
    	}

        // Check if the table exists in the database and other wise create it
        LOG.info("Checking database tables...");
        try {
            for (String tableName : tables.keySet()) {
                if (!DatabaseUtils.tableExists(connection, tableName)) {
                    LOG.info(tableName + " not found, adding...");
                    statement.execute(tables.get(tableName));
                } else {
                    LOG.info(tableName + " already exists.");
                }
            }
        } catch (DatabaseException e) {
            LOG.severe("Error checking the table: " + e.getLocalizedMessage());
            throw e;
        } catch (SQLException e) {
            LOG.severe("Error executing statement: " + e.getLocalizedMessage());
            throw new DatabaseException("Error executing statement: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Stores the sendable in the {@link Sendable} object into the database in the table
     * pointed to in the entity class.
     *
     * Sendable class must use the entity class annotations from the JPA.
     *
     * @param sendable Sendable object to store in the database in the appropriate table
     */
    public void store(Sendable sendable) {
        EntityManager entityManager = factory.createEntityManager();

        // Create transaction for storing the sendable
        EntityTransaction tx = entityManager.getTransaction();

        // Start the transaction and store the sendable
        tx.begin();
        entityManager.persist(sendable);

        // Commit and close the entity manager
        tx.commit();
        entityManager.close();

    }

    /**
     * Retrieves all sendable from the database for the player ID given and parses it
     * into a list of the entity class searched for.
     *
     * @param entityClass Class type to search for
     * @param playerID Player ID to search the database for
     * @param <T> Class type to search for
     * @return List of the result set from the database query
     */
    public <T> List<T> retrieve(Class<T> entityClass, int playerID) {
        return retrieve(entityClass, playerID, null, null);
    }

    /**
     * Retrieves all sendable from the database for the player ID given if
     * its entry time was past the start time.
     *
     * Parses the results into a list of the entity class searched for.
     *
     * @param entityClass Class type to search for
     * @param playerID Player ID to search the database for
     * @param startTime Date of the start time to search from
     * @param <T> Class type to search for
     * @return List of the result set from the database query
     */
    public <T> List<T> retrieve(Class<T> entityClass, int playerID, Date startTime) {
    	return retrieve(entityClass, playerID, startTime, null);
    }

    /**
     * Retrieves all sendable from the database for the player ID given if
     * its entry time was past the start time and before the end time.
     *
     * Parses the results into a list of the entity class searched for.
     *
     * @param entityClass Class type to search for
     * @param playerID Player ID to search the database for
     * @param startTime Date of the start time to search from
     * @param endTime Date of the end time to search to
     * @param <T> Class type to search for
     * @return List of the result set from the database query
     */
    public <T> List<T> retrieve(Class<T> entityClass, int playerID, Date startTime, Date endTime) {
        // Create entity manager
    	EntityManager entityManager = factory.createEntityManager();

        // Create the criteria objects for the query
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
    	Root<T> root = criteriaQuery.from(entityClass);

        // Create a list of predicates to use in the query
    	List<Predicate> predicates = new ArrayList<Predicate>();
    	
    	// Add the start time to the predicates list if it exists
    	if (startTime != null) {
    		predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("time"), startTime));
    	}

        // Add the end time to the predicates list if it exists
    	if (endTime != null) {
    		predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("time"), endTime));
    	}

        // Set the player ID to search for
    	predicates.add(criteriaBuilder.equal(root.get("UID"), playerID));

        // Query the sendable from the database using the predicates
    	criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
    	TypedQuery<T> typed = entityManager.createQuery(criteriaQuery);
        return typed.getResultList();
    }

    /**
     * Removes the entity given in the sendable object from the database
     * if it exists.
     *
     * @param sendable Sendable object to remove from the database
     */
    public void remove(Sendable sendable) {
        EntityManager entityManager = factory.createEntityManager();

        // Create transaction for storing the sendable
        EntityTransaction tx = entityManager.getTransaction();

        // Start the transaction and remove the object
        tx.begin();
        entityManager.remove(sendable);

        // Commit and close the EM
        tx.commit();
        entityManager.close();
    }

    /**
     * Close the connection to the embedded Derby server and shutdown the server.
     *
     * @throws DatabaseException
     */
    public void shutdown() throws DatabaseException {
        // Shutdown factory
        if (factory != null) {
            factory.close();
        }

        // Check there is a connection to close
        if (connection == null) {
            LOG.warning("No connection to shutdown.");
            return;
        }

        // Close the connection to the database
        try {
            LOG.info("Closing connection...");
            connection.close();
        } catch (SQLException e) {
            LOG.severe("Could not close the connection to the db: " + e.getLocalizedMessage());
            throw new DatabaseException("Could not close the connection to the db: " + e.getLocalizedMessage(), e);
        }

        // Shut down the embedded Derby server
        try {
            LOG.info("Shutting down embedded Derby server...");
            DriverManager.getConnection(shutdownURL);
        } catch (SQLException e)  {
            // Check the state is the correct error state
            if (e.getSQLState().equals("XJ015")) {
                LOG.info("Shutdown successful!");
            } else {
                LOG.severe("Could not shutdown Derby properly: " + e.getLocalizedMessage());
                throw new DatabaseException("Could not shutdown Derby properly: " + e.getLocalizedMessage(), e);
            }
        }
    }

    public static void main(String args[]) {
        Database db = new Database();
        try {
            Acceleration acceleration = new Acceleration(16, new Date(), 1, 1, 1, 1);
            Position position1 = new Position(16, new Date(), 1, 2, 3);

            Date test = new Date();

            Acceleration acceleration2 = new Acceleration(16, new Date(), 100, 8100, 6100, 2100);
            Position position2 = new Position(16, new Date(), 4, 5, 6);
            Alarm alarm = new Alarm(10, new Date(), new PlayerCause(10));
            Alarm alarm2 = new Alarm(10, new Date(), new TrainerCause(Priority.MAJOR));
            Alarm alarm3 = new Alarm(10, new Date(), new DataCause(100));

            db.connect();
            db.init();
            db.store(acceleration);
            db.store(acceleration2);
            db.store(position1);
            db.store(position2);

            LOG.info("=== STORING ALARM DATA ===");
            db.store(alarm);
            LOG.info("=== STORING ALARM2 DATA ===");
            db.store(alarm2);
            LOG.info("=== STORING ALARM3 DATA ===");
            db.store(alarm3);

            LOG.info("Retrieving all accelerations for uid 16: ");
            List<Alarm> alarms = db.retrieve(Alarm.class, 10);
            for (Alarm a : alarms) {
                System.out.println(a.getUID() + ", " + a.getTime() + ", " + (a.getCause() instanceof PlayerCause) +
                                    ", " + (a.getCause() instanceof TrainerCause) + ", " + (a.getCause() instanceof DataCause));
            }
            
            LOG.info("Retrieving all accelerations for uid 16: ");
            List<Acceleration> accelerations = db.retrieve(Acceleration.class, 16);
            for(Acceleration accel : accelerations) {
                System.out.println(accel.getUID() + ", " + accel.getTime() + ", " + accel.getxAccel() + ", " +
                                   accel.getyAccel() + ", " + accel.getzAccel() + ", " + accel.getAccelMag());
            }
            
            LOG.info("Retrieving all accelerations for uid 16 past date " + test + ": ");
            accelerations = db.retrieve(Acceleration.class, 16, test);
            for(Acceleration accel : accelerations) {
                System.out.println(accel.getUID() + ", " + accel.getTime() + ", " + accel.getxAccel() + ", " +
                                   accel.getyAccel() + ", " + accel.getzAccel() + ", " + accel.getAccelMag());
            }

            LOG.info("Retrieving all positions for uid 16: ");
            List<Position> positions = db.retrieve(Position.class, 16);
            for(Position position : positions) {
                System.out.println(position.getUID() + ", " + position.getTime() + ", " + position.getxPos() + ", " +
                        position.getyPos() + ", " + position.getzPos());
            }

            LOG.info("Retrieving all positions for uid 16 past date " + test + ": ");
            positions = db.retrieve(Position.class, 16, test);
            for(Position position : positions) {
                System.out.println(position.getUID() + ", " + position.getTime() + ", " + position.getxPos() + ", " +
                        position.getyPos() + ", " + position.getzPos());
            }

            db.shutdown();
        } catch (DatabaseException e) {
            LOG.severe(e.getLocalizedMessage());
        }
    }
}

package database;

import sendable.*;
import exception.DatabaseException;

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
    private static final long NULL = -1;
    private Connection connection = null;
    private Map<String, String> tables;
    private EntityManagerFactory factory;

    public Database() {
        // Create map that holds table creation strings
        tables = new HashMap<String, String>();
        tables.put("ALARMDATA", "CREATE TABLE ALARMDATA " +
                                "(INDEX INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                                " UID INT NOT NULL," +
                                " TIME BIGINT NOT NULL, " +
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
                                " TIME BIGINT NOT NULL," +
                                " X_ACCEL DOUBLE PRECISION NOT NULL," +
                                " Y_ACCEL DOUBLE PRECISION NOT NULL," +
                                " Z_ACCEL DOUBLE PRECISION NOT NULL," +
                                " ACCEL DOUBLE PRECISION NOT NULL)");
        tables.put("POSDATA", "CREATE TABLE POSDATA " +
                              "(INDEX INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                              " PLAYERID INT NOT NULL," +
                              " TIME BIGINT NOT NULL," +
                              " X_POS DOUBLE PRECISION NOT NULL," +
                              " Y_POS DOUBLE PRECISION NOT NULL," +
                              " Z_POS DOUBLE PRECISION NOT NULL)");

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
            Class.forName(driverClass).newInstance();

            // Create the connection
            connection = DriverManager.getConnection(connectionURL);

            LOG.info("Connected.");
        } catch (ClassNotFoundException e) {
            LOG.severe("Could not find driver class: " + e.getLocalizedMessage());
            throw new DatabaseException("Could not find driver class: " + e.getLocalizedMessage(), e);
        } catch (InstantiationException e) {
            LOG.severe("Could not instantiate driver class: " + e.getLocalizedMessage());
            throw new DatabaseException("Could not instantiate driver class: " + e.getLocalizedMessage(), e);
        } catch(IllegalAccessException e) {
            LOG.severe(e.getLocalizedMessage());
            throw new DatabaseException(e.getLocalizedMessage(), e);
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
        return retrieve(entityClass, playerID, NULL, NULL);
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
    public <T> List<T> retrieve(Class<T> entityClass, int playerID, long startTime) {
    	return retrieve(entityClass, playerID, startTime, NULL);
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
    public <T> List<T> retrieve(Class<T> entityClass, int playerID, long startTime, long endTime) {
        // Create entity manager
    	EntityManager entityManager = factory.createEntityManager();

        // Create the criteria objects for the query
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
    	Root<T> root = criteriaQuery.from(entityClass);

        // Create a list of predicates to use in the query
    	List<Predicate> predicates = new ArrayList<Predicate>();
    	
    	// Add the start time to the predicates list if it exists
    	if (startTime != NULL) {
    		predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Long>get("time"), startTime));
    	}

        // Add the end time to the predicates list if it exists
    	if (endTime != NULL) {
    		predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Long>get("time"), endTime));
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
        Sendable toBeRemoved = entityManager.merge(sendable);
        entityManager.remove(toBeRemoved);

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
}

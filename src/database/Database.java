package database;

/**
 * Class that will manage database driver and connection.
 *
 * @version 1
 */

import data.Acceleration;
import data.Sendable;
import exception.DatabaseException;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

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
        tables = new HashMap<String, String>();
        tables.put("ALARMDATA", "CREATE TABLE ALARMDATA " +
                                "(PLAYERID INT NOT NULL PRIMARY KEY," +
                                " ENTRY_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                                " LEVEL INT NOT NULL)");
        tables.put("ACCELDATA", "CREATE TABLE ACCELDATA " +
                                "(UID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                                " PLAYERID INT NOT NULL," +
                                " TIME TIMESTAMP NOT NULL," +
                                " X_ACCEL INT NOT NULL," +
                                " Y_ACCEL INT NOT NULL," +
                                " Z_ACCEL INT NOT NULL," +
                                " ACCEL INT NOT NULL)");
        factory = Persistence.createEntityManagerFactory("sdb");
    }

    /**
     * Creates a connection with the database if there is not one already.
     *
     * @throws DatabaseException
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
     * @throws DatabaseException
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

    public void store(Sendable sendable) {
        LOG.info("Storing data...");
        EntityManager entityManager = factory.createEntityManager();

        // Create transaction for storing the data
        EntityTransaction tx = entityManager.getTransaction();

        // Start the transaction and store the data
        tx.begin();
        entityManager.persist(sendable);
        tx.commit();

        LOG.info("Finished adding data.");
    }

    public <T> List<T> retrieve(Class<T> entityClass, int primaryKey) throws DatabaseException {
        return retrieve(entityClass, primaryKey, null, null);
    }
    
    public <T> List<T> retrieve(Class<T> entityClass, int primaryKey, Date startTime) {
    	return retrieve(entityClass, primaryKey, startTime, null);
    }
    
    public <T> List<T> retrieve(Class<T> entityClass, int primaryKey, Date startTime, Date endTime) {
    	EntityManager entityManager = factory.createEntityManager();
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
    	Root<T> root = criteriaQuery.from(entityClass);
    	List<Predicate> predicates = new ArrayList<Predicate>();
    	
    	// If there is no start time, get all the data no matter the time stamp
    	if (startTime != null) {
    		predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("time"), startTime));
    	}
    	
    	// If there exists a specified end time
    	if (endTime != null) {
    		predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("time"), endTime));
    	}
    	
    	predicates.add(criteriaBuilder.equal(root.get("UID"), primaryKey));
    	
    	criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
    	TypedQuery<T> typed = entityManager.createQuery(criteriaQuery);
        return typed.getResultList();
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
        	Date test = new Date();
            Acceleration acceleration = new Acceleration(16, new Date(), 1, 1, 1, 1);
            Acceleration acceleration2 = new Acceleration(18, new Date(), 100, 8100, 6100, 2100);
            
            db.connect();
            db.init();
            db.store(acceleration);
            db.store(acceleration2);
            
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

            db.shutdown();
        } catch (DatabaseException e) {
            LOG.severe(e.getLocalizedMessage());
        }
    }
}

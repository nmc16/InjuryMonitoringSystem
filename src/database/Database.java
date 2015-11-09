package database;

/**
 * Class that will manage database driver and connection.
 *
 * @author N McCallum
 * @version 1
 */

import data.Sendable;
import exception.DatabaseException;

import java.sql.*;
import java.util.logging.Logger;

public class Database {
    private static final String driverClass = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String dbName = "sensorDB";
    private static final String connectionURL = "jdbc:derby:" + dbName + ";create=true";
    private static final String shutdownURL = "jdbc:derby:;shutdown=true";
    private static final Logger LOG = Logger.getLogger("DBLogger");
    private static Connection connection = null;

    /**
     * Creates a connection with the database if there is not one already.
     *
     * @throws DatabaseException
     */
    public static void connect() throws DatabaseException {
        // Check if there is already a connection
        if (connection != null) {
            LOG.config("Already connected to database.");
            return;
        }

        // If there isn't a connection try and create one
        try {
            LOG.config("Connecting to database...");

            // Load the database driver
            Class.forName(driverClass);

            // Create the connection
            connection = DriverManager.getConnection(connectionURL);

            LOG.config("Connected.");
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
    public static void init() throws DatabaseException {
        // TODO Create tables in the database
    }

    public static void store(Sendable sendable) throws DatabaseException {
        // Store into table in database
    }

    /**
     * Close the connection to the embedded Derby server and shutdown the server.
     *
     * @throws DatabaseException
     */
    public static void shutdown() throws DatabaseException {
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

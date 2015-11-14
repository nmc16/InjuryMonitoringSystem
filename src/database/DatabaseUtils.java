package database;

import java.sql.*;
import exception.DatabaseException;

/**
 * Utility class for the database controller that can check if a table exists in the
 * database already.
 *
 * @version 1
 * @see Database
 */
public class DatabaseUtils {

    /**
     * Checks if table exists already in the database.
     *
     * @param connection SQL Connection to perform the check over
     * @param sql SQL statement of the table definition to check for
     * @return Boolean value representing if the table exists or not
     * @throws DatabaseException Thrown when an SQL error occurs during the request
     */
	public static boolean tableExists(Connection connection, String sql) throws DatabaseException {
		try {
			ResultSet results = connection.getMetaData().getTables(null, null, sql, new String[] {"TABLE"});
			return results.next();
		} catch (SQLException e) {
			if (e.getSQLState().equals("42X05")) {
				// If the error is the table does not exist return false
				return false;
			} else if (e.getSQLState().equals("42X14") ||e.getSQLState().equals("42821")) {
				throw new DatabaseException("Incorrect table definition: " + e.getLocalizedMessage(), e);
			} else {
				throw new DatabaseException("Exception occurred receiving table: " + e.getLocalizedMessage(), e);
			}
		}
	}
}

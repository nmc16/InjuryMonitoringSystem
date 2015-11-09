package database;

import java.sql.*;
import exception.DatabaseException;

public class DatabaseUtils {
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
				throw new DatabaseException("Exception occured retreiving table: " + e.getLocalizedMessage(), e);
			}
		}
	}
}

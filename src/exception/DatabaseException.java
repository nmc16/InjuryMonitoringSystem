package exception;

/**
 * Exception that occurs when there is an error in database operations.
 *
 * @author N McCallum
 * @version 1
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

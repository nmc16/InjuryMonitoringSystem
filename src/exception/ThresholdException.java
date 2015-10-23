package exception;

/**
 * Exception to be thrown by the controller if the value calculated
 * for the acceleration crosses the threshold for warning.
 *
 * @version 1
 */
public class ThresholdException extends Exception {
    public ThresholdException(String message) {
        super(message);
    }
}

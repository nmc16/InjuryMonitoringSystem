package exception;

import sendable.data.Acceleration;

/**
 * Exception to be thrown by the controller if the value calculated
 * for the acceleration crosses the threshold for warning.
 *
 * @version 1
 */
public class ThresholdException extends Exception {
    private Acceleration acceleration;

    public ThresholdException(String message, Acceleration acceleration) {
        super(message);
        this.acceleration = acceleration;
    }

    public Acceleration getAcceleration() {
        return acceleration;
    }
}

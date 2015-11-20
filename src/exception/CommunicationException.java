package exception;

/**
 * Exception that occurs when there is an error in operations performed
 * by a {@link controller.Producer}
 *
 * @version 1
 */
public class CommunicationException extends Exception {

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String messsage, Throwable cause) {
        super(messsage, cause);
    }
}

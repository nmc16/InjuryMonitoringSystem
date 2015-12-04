package sendable;

import java.util.Date;

/**
 * Interface for serializable object that can be sent or received
 * by a producer or consumer.
 *
 * @version 1
 */
public interface Sendable {


    int getType();

    /**
     * Set the unique identifier of the sendable object. Should be the
     * identifier of the PI/Host that the data is being sent from
     * (i.e. Sensor ID or Host ID).
     *
     * @param uid Unique identifier of the sender
     */
    void setUID(int uid);

    /**
     * Set the time the object was created or edited. The time should
     * be the time from epoch.
     *
     * @param time time from epoch
     */
    void setTime(long time);

    /**
     * Get the unique identifier of the sender of the object.
     *
     * @return returns the UID of the sender
     */
    int getUID();

    /**
     * Get the long representing the time from epoch when the data was last
     * edited.
     *
     * @return time from epoch
     */
    long getTime();

    /**
     * Get the date object using the time from epoch.
     *
     * @return new date object from the time from epoch
     */
    Date getDate();
}

package data;

import java.util.Date;

/**
 * Interface for serializable object that can be sent or received
 * by a producer or consumer.
 *
 * @version 1
 */
public interface Sendable {
    void setUID(int uid);
    void setTime(Date time);
    int getUID();
    Date getTime();
}

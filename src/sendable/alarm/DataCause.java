package sendable.alarm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Cause for if one of the thresholds is crossed in the controller reading
 * the acceleration sendable from the sensor.
 *
 * Holds the threshold that was crossed to generate the alarm.
 *
 * @version 1
 */
@Entity
@Table(name = "CAUSE")
public class DataCause extends Cause {
    private int threshold;

    public DataCause() {}

    public DataCause(int threshold) {
        this.threshold = threshold;
    }

    @Column(name = "THRESHOLD")
    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}

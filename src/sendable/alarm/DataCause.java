package sendable.alarm;

import sendable.DataType;

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

    public DataCause() {
        super();
        type = DataType.CAUSE_DATA;
    }

    public DataCause(int threshold) {
        this();
        this.threshold = threshold;
    }

    public DataCause(int threshold, String message) {
        super(message);
        this.threshold = threshold;
        type = DataType.CAUSE_DATA;
    }

    @Column(name = "THRESHOLD")
    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataCause dataCause = (DataCause) o;

        return threshold == dataCause.getThreshold() && super.equals(dataCause);
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 37 * result + super.hashCode();
        result = 37 * result + threshold;
        return result;
    }
}

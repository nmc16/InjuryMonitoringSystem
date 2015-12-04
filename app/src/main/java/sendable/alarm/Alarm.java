package sendable.alarm;

import sendable.DataType;
import sendable.Sendable;

import javax.persistence.*;
import java.util.Date;

/**
 * Holds alarm sendable that can be sent to the emergency controller and
 * the host.
 *
 * Entity class that can be stored in the database under the table ALARMDATA.
 * Requires the CAUSE table to store the embedded entity object {@link Cause}.
 *
 * @version 1
 */
@Entity
@Table(name = "ALARMDATA")
public class Alarm implements Sendable {
    private final int type;
    private int tableID;
    private int uid;
    private long time;
    private Cause cause;

    public Alarm() {
        this.type = DataType.ALARM;
    }

    public Alarm(int uid, long time, Cause cause) {
        this();
        this.uid = uid;
        this.time = time;
        this.cause = cause;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDEX")
    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }


    @OneToOne(cascade = CascadeType.PERSIST)
    public Cause getCause() {
        return cause;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setUID(int uid) {
        this.uid = uid;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    @Column(name = "UID", nullable = false)
    public int getUID() {
        return uid;
    }

    @Override
    @Column(name = "TIME", nullable = false)
    public long getTime() {
        return time;
    }

    @Override
    public Date getDate() {
        return new Date(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        return uid == alarm.getUID() &&
               time == alarm.getTime() &&
               cause != null ? cause.equals(alarm.getCause()) : alarm.getCause() == null;

    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 37 * result + Long.valueOf(time).hashCode();
        result = 37 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return cause.toString();
    }
}

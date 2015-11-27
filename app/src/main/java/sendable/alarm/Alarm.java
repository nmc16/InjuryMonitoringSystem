package sendable.alarm;

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
    private int tableID;
    private int uid;
    private Date time;
    private Cause cause;

    public Alarm() {}

    public Alarm(int uid, Date time, Cause cause) {
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
    public void setUID(int uid) {
        this.uid = uid;
    }

    @Override
    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    @Column(name = "UID", nullable = false)
    public int getUID() {
        return uid;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME", nullable = false)
    public Date getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        return uid == alarm.getUID() &&
               time != null ? time.equals(alarm.getTime()) : alarm.getTime() == null &&
               cause != null ? cause.equals(alarm.getCause()) : alarm.getCause() == null;

    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 37 * result + (time != null ? time.hashCode() : 0);
        result = 37 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }
}

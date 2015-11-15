package sendable.data;

import sendable.Sendable;

import javax.persistence.*;
import java.util.Date;

/**
 * Class to track the x, y, and z co-ordinates of the accelerometer in order
 * to calculate the acceleration sendable.
 *
 * @version 1
 */
@Entity
@Table(name = "POSDATA")
public class Position implements Sendable {
    private int tableID;
    private int uid;
    private Date time;
    private int xPos;
    private int yPos;
    private int zPos;

    public Position() {
        this(0, new Date(), 0, 0, 0);
    }

    public Position(int uid, Date time, int xPos, int yPos, int zPos) {
        this.uid = uid;
        this.time = time;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    @Column(name = "X_POS", nullable = false)
    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    @Column(name = "Y_POS", nullable = false)
    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    @Column(name = "Z_POS", nullable = false)
    public int getzPos() {
        return zPos;
    }

    public void setzPos(int zPos) {
        this.zPos = zPos;
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
    @Column(name = "PLAYERID", nullable = false)
    public int getUID() {
        return uid;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME", nullable = false)
    public Date getTime() {
        return time;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        return uid == position.getUID() &&
               xPos == position.getxPos() &&
               yPos == position.getyPos() &&
               zPos == position.getzPos() &&
               time != null ? time.equals(position.getTime()) : position.getTime() == null;
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 37 * result + uid;
        result = 37 * result + (time != null ? time.hashCode() : 0);
        result = 37 * result + xPos;
        result = 37 * result + yPos;
        result = 37 * result + zPos;
        return result;
    }
}

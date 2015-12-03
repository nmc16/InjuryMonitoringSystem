package sendable.data;

import sendable.DataType;
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
    private final int type;
    private int tableID;
    private int uid;
    private long time;
    private double xPos;
    private double yPos;
    private double zPos;

    public Position() {
        this(0, 0, 0, 0, 0);
    }

    public Position(int uid, long time, double xPos, double yPos, double zPos) {
        this.uid = uid;
        this.time = time;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.type = DataType.POS;
    }

    @Column(name = "X_POS", nullable = false)
    public double getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    @Column(name = "Y_POS", nullable = false)
    public double getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    @Column(name = "Z_POS", nullable = false)
    public double getzPos() {
        return zPos;
    }

    public void setzPos(int zPos) {
        this.zPos = zPos;
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
    @Column(name = "PLAYERID", nullable = false)
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
               time == position.getTime();
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 37 * result + uid;
        result = 37 * result + Long.valueOf(time).hashCode();
        result = (int) (37 * result + xPos);
        result = (int) (37 * result + yPos);
        result = (int) (37 * result + zPos);
        return result;
    }
}

package data;

import javax.persistence.*;

import java.util.Date;

/**
 * Class that holds the acceleration data calculated by the controller
 * for all the axes (i.e. X, Y, and Z) and an overall magnitude for the
 * acceleration.
 *
 * Also holds player ID that is unique for each sensor and the time
 * the acceleration was calculated from.
 *
 * @version 1
 */
@Entity
@Table(name = "ACCELDATA")
public class Acceleration implements Sendable {
	private int tableID;
    private int uid;
    private Date time;
    private int xAccel;
    private int yAccel;
    private int zAccel;
    private int accelMag;

    public Acceleration() {
        this(0, new Date(), 0, 0, 0, 0);
    }

    public Acceleration(int uid, Date time, int xAccel, int yAccel, int zAccel, double accelMag) {
        this(uid, time, xAccel, yAccel, zAccel, (int) accelMag);
    }

    public Acceleration(int uid, Date time, int xAccel, int yAccel, int zAccel, int accelMag) {
        this.uid = uid;
        this.time = time;
        this.xAccel = xAccel;
        this.yAccel = yAccel;
        this.zAccel = zAccel;
        this.accelMag = accelMag;
    }

    @Column(name = "X_ACCEL", nullable = false)
    public int getxAccel() {
        return xAccel;
    }

    public void setxAccel(int xAccel) {
        this.xAccel = xAccel;
    }

    @Column(name = "Y_ACCEL", nullable = false)
    public int getyAccel() {
        return yAccel;
    }

    public void setyAccel(int yAccel) {
        this.yAccel = yAccel;
    }

    @Column(name = "Z_ACCEL", nullable = false)
    public int getzAccel() {
        return zAccel;
    }

    public void setzAccel(int zAccel) {
        this.zAccel = zAccel;
    }

    @Column(name = "ACCEL", nullable = false)
    public int getAccelMag() {
        return accelMag;
    }

    public void setAccelMag(int accelMag) {
        this.accelMag = accelMag;
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
}

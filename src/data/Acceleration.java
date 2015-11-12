package data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class that holds the acceleration data calculated by the controller
 * for all the axes (i.e. X, Y, and Z).
 *
 * @version 1
 */
@Entity
@Table(name = "ACCELERATION")
public class Acceleration {
    private int xAccel;
    private int yAccel;
    private int zAccel;
    private int accelMag;

    public Acceleration() {
        this(0, 0, 0, 0);
    }

    public Acceleration(int xAccel, int yAccel, int zAccel, double accelMag) {
        this(xAccel, yAccel, zAccel, (int) accelMag);
    }

    public Acceleration(int xAccel, int yAccel, int zAccel, int accelMag) {
        this.xAccel = xAccel;
        this.yAccel = yAccel;
        this.zAccel = zAccel;
        this.accelMag = accelMag;
    }

    @Id
    @Column(name = "X_ACCEL")
    public int getxAccel() {
        return xAccel;
    }

    public void setxAccel(int xAccel) {
        this.xAccel = xAccel;
    }

    @Column(name = "Y_ACCEL")
    public int getyAccel() {
        return yAccel;
    }

    public void setyAccel(int yAccel) {
        this.yAccel = yAccel;
    }

    @Column(name = "Z_ACCEL")
    public int getzAccel() {
        return zAccel;
    }

    public void setzAccel(int zAccel) {
        this.zAccel = zAccel;
    }

    @Column(name = "ACCEL")
    public int getAccelMag() {
        return accelMag;
    }

    public void setAccelMag(int accelMag) {
        this.accelMag = accelMag;
    }
}

package data;

import javax.persistence.*;
import java.util.Date;

/**
 * Class to hold the data read from the sensor.
 *
 * @version 1
 */
@Entity
public class SensorData {
    private Position position;
    private Acceleration acceleration;
    private int uid;
    private Date date;

    public SensorData(int x, int y, int z) {
        position = new Position(0, new Date(), x, y, z);
    }

    public SensorData(int x, int y, int z, int xAccel, int yAccel, int zAccel, int accelMag) {
        position = new Position(0, new Date(), x, y, z);
    }

    public SensorData(Position position) {
        this.position = position;
    }

    public SensorData(Position position, Acceleration acceleration) {
        this.position = position;
        this.acceleration = acceleration;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Acceleration getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Acceleration acceleration) {
        this.acceleration = acceleration;
    }



    @Id
    @Column(name = "PLAYERID", unique = true, nullable = false)
    public int getUID() {
        return uid;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME", nullable = false)
    public Date getTime() {
        return date;
    }
}

package data;

import javax.persistence.*;
import java.util.Date;

/**
 * Class to hold the data read from the sensor.
 *
 * @version 1
 */
@Entity
public class SensorData implements Sendable {
    private Position position;
    private Acceleration acceleration;
    private int uid;
    private Date date;

    public SensorData(int x, int y, int z) {
        position = new Position(x, y, z);
        acceleration = new Acceleration(0, 0, 0, 0);
        setTime();
    }

    public SensorData(int x, int y, int z, int xAccel, int yAccel, int zAccel, int accelMag) {
        position = new Position(x, y, z);
        acceleration = new Acceleration(xAccel, yAccel, zAccel, accelMag);
        setTime();
    }

    public SensorData(Position position) {
        this.position = position;
        this.acceleration = new Acceleration(0, 0, 0, 0);
        setTime();
    }

    public SensorData(Position position, Acceleration acceleration) {
        this.position = position;
        this.acceleration = acceleration;
        setTime();
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

    @Override
    public void createUID() {
        uid = this.hashCode();
    }

    @Override
    public void setTime() {
        date = new Date();
    }

    @Override
    @Id
    @Column(name = "PLAYER_ID", unique = true, nullable = false)
    public int getUID() {
        return uid;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME", nullable = false)
    public Date getTime() {
        return date;
    }
}

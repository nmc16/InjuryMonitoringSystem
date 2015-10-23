package data;

import java.util.Date;

/**
 * Class to hold the data read from the sensor.
 *
 * @version 1
 */
public class SensorData implements Sendable {
    private Position position;
    private int uid;
    private Date date;

    public SensorData(int x, int y, int z) {
        position = new Position(x, y, z);
        setTime();
    }

    public SensorData(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
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
    public int getUID() {
        return uid;
    }

    @Override
    public Date getTime() {
        return date;
    }
}

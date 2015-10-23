package data;

/**
 * Sensor data that will be stored in the database. Extends {@link data.SensorData} to get
 * all of the other needed attributes.
 *
 * @version 1
 * @see data.SensorData
 */
public class SensorDBData extends SensorData {
    private double accel;

    public SensorDBData(Position position, double accel) {
        super(position);
        this.accel = accel;
    }

    public SensorDBData(int x, int y, int z, double accel) {
        super(x, y, z);
        this.accel = accel;
    }

    public void setAccel(double accel) {
        this.accel = accel;
    }

    public double getAccel() {
        return accel;
    }

}

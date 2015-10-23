package controller;

/**
 * Database controller to handle the calculation of data.
 *
 * @version 1
 */
import data.SensorDBData;
import data.SensorData;
import exception.ThresholdException;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class Controller {

    private double threshold;

    public Controller(double threshold) {
        this.threshold = threshold;
    }

    public SensorDBData calculate(SensorData s1, SensorData s2) throws ThresholdException {
        // Calculate the acceleration data
        int deltaX = s2.getPosition().getxPos() - s1.getPosition().getxPos();
        int deltaY = s2.getPosition().getyPos() - s1.getPosition().getyPos();
        int deltaZ = s2.getPosition().getzPos() - s1.getPosition().getzPos();

        double accel = sqrt(pow(deltaX, 2) + pow(deltaY, 2) + pow(deltaZ, 2));

        if (accel >= threshold) {
            throw new ThresholdException("Threshold value exceeded: " + accel);
        }

        // Create new DB Data object with new acceleration data and return
        return new SensorDBData(s1.getPosition(), accel);
    }
}

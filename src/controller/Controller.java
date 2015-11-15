package controller;

/**
 * Database controller to handle the calculation of sendable.
 *
 * @version 1
 */
import sendable.data.Acceleration;
import exception.ThresholdException;
import sendable.data.Position;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class Controller {

    private double threshold;

    public Controller(double threshold) {
        this.threshold = threshold;
    }

    public Acceleration calculate(Position p1, Position p2) throws ThresholdException {
        // Calculate the acceleration sendable
        int deltaX = p2.getxPos() - p1.getxPos();
        int deltaY = p2.getyPos() - p1.getyPos();
        int deltaZ = p2.getzPos() - p1.getzPos();

        int xAccel = (int) (deltaX / ((p2.getTime().getTime() - p1.getTime().getTime()) / 1000));
        int yAccel = (int) (deltaY / ((p2.getTime().getTime() - p1.getTime().getTime()) / 1000));
        int zAccel = (int) (deltaZ / ((p2.getTime().getTime() - p1.getTime().getTime()) / 1000));

        double accel = sqrt(pow(deltaX, 2) + pow(deltaY, 2) + pow(deltaZ, 2));

        if (accel >= threshold) {
            throw new ThresholdException("Threshold value exceeded: " + accel);
        }

        // Create new DB Data object with new acceleration sendable and return
        return new Acceleration(p2.getUID(), p2.getTime(), xAccel, yAccel, zAccel, accel);
    }
}

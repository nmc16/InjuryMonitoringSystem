package data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Class to track the x, y, and z co-ordinates of the accelerometer in order
 * to calculate the acceleration data.
 *
 * @version 1
 */
@Entity
public class Position implements Serializable {
    private int xPos;
    private int yPos;
    private int zPos;

    public Position() {
        this(0, 0, 0);
    }

    public Position(int xPos, int yPos, int zPos) {
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
}

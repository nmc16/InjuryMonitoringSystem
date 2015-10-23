package data;

/**
 * Class to track the x, y, and z co-ordinates of the accelerometer in order
 * to calculate the acceleration data.
 *
 * @version 1
 */
public class Position {
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

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getzPos() {
        return zPos;
    }

    public void setzPos(int zPos) {
        this.zPos = zPos;
    }
}

package sendable.alarm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Cause if the emergency button on the player sensor is pressed.
 *
 * Holds the player ID for the sensor that the button was pressed
 * on.
 *
 * @version 1
 */
@Entity
@Table(name = "CAUSE")
public class PlayerCause extends Cause {
    private int playerID;

    public PlayerCause() {}

    public PlayerCause(int playerID) {
        this.playerID = playerID;
    }

    @Column(name = "PLAYERID")
    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}

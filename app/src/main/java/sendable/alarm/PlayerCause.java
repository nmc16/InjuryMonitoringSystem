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

    public PlayerCause() {
        super();
    }

    public PlayerCause(int playerID) {
        super();
        this.playerID = playerID;
    }

    public PlayerCause(int playerID, String message) {
        super(message);
        this.playerID = playerID;
    }

    @Column(name = "PLAYERID")
    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerCause playerCause = (PlayerCause) o;

        return playerID == playerCause.getPlayerID() && super.equals(playerCause);
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 37 * result + super.hashCode();
        result = 37 * result + playerID;
        return result;
    }
}

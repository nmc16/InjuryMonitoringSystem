package sendable.alarm;

import javax.persistence.*;

/**
 * Holds the cause for the generated alarm. Data is stored in the
 * CAUSE table in the database.
 *
 * @version 1
 */
@Entity
public class Cause {
    private int tableID;
    private String message;

    public Cause() {
        message = "";
    }

    public Cause(String message) {
        this.message = message;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDEX")
    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    @Column(name = "MSG")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

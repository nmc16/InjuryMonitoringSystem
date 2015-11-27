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
    protected String message;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataCause dataCause = (DataCause) o;

        return message != null ? message.equals(dataCause.getMessage()) : dataCause.getMessage() == null;
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 37 * result + (message != null ? message.hashCode(): 0);
        return result;
    }
}

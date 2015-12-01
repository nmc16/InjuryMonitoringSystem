package sendable.alarm;

import sendable.DataType;

import javax.persistence.*;

/**
 * Cause if the trainer request additional assistance from the emergency controller.
 *
 * Holds the priority of the request.
 *
 * @version 1
 */
@Entity
@Table(name = "CAUSE")
public class TrainerCause extends Cause {
    private Priority priority;

    public TrainerCause() {
        super();
        type = DataType.CAUSE_TRAINER;
    }

    public TrainerCause(Priority priority) {
        this();
        this.priority = priority;
    }

    public TrainerCause(Priority priority, String message) {
        super(message);
        this.priority = priority;
        type = DataType.CAUSE_TRAINER;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORITY")
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainerCause trainerCause = (TrainerCause) o;

        return priority == trainerCause.getPriority() && super.equals(trainerCause);

    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 37 * result + super.hashCode();
        result = 37 * result + (priority != null ? priority.hashCode() : 0);
        return result;
    }
}

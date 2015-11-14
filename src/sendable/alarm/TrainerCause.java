package sendable.alarm;

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

    public TrainerCause() {}

    public TrainerCause(Priority priority) {
        this.priority = priority;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORITY")
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}

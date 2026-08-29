/**
 * Represents a task with a specific deadline.
 * Extends Task to add a deadline field, displayed when the task is converted to a string.
 */
public class Deadline extends Task {
    private final String deadline;

    /**
     * Constructs a Deadline task with the given description and deadline.
     *
     * @param description The description of the deadline task
     * @param deadline The deadline by which the task should be completed
     */
    public Deadline(String description, String deadline) {
        super(description, TYPE_DEADLINE);
        this.deadline = deadline;
    }

    /**
     * Returns a string representation of the deadline task.
     * Appends the deadline in the format "(by: <deadline>)" to the base task description.
     *
     * @return The formatted string with task description and deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.deadline + ")";
    }
}

package moistbot.task;

/**
 * Represents a task that occurs during a specific time period.
 * Extends Task to add start and end time fields, displayed when the task is converted to a string.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Constructs an Event task with the given description, start time, and end time.
     *
     * @param description The description of the event
     * @param from The start time of the event
     * @param to The end time of the event
     */
    public Event(String description, String from, String to) {
        super(description, TYPE_EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns a string representation of the event task.
     * Appends the time period in the format {@code (from: <start> to: <end>)} to the task description.
     *
     * @return The formatted string with task description and time period
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }
}

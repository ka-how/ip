/**
 * Represents a single task in the task list.
 * Each task has a description and a completion status that can be toggled.
 * Tasks can be of different types (todo, deadline, event) identified by a character code.
 */
public class Task {
    public static final char TYPE_TODO = 'T';
    public static final char TYPE_DEADLINE = 'D';
    public static final char TYPE_EVENT = 'E';

    private String description;
    private boolean isCompleted;
    private char taskType;

    /**
     * Constructs a Task with the given description and task type.
     * Tasks are initialized as not completed.
     *
     * @param description The description of the task
     * @param taskType The type of the task: T for todo, D for deadline, or E for event
     */
    public Task(String description, char taskType) {
        this.description = description;
        this.isCompleted = false;
        this.taskType = taskType;
    }

    /**
     * Constructs a generic todo task.
     * This is a convenience constructor that creates a Task of type TODO.
     *
     * @param description The description of the task
     */
    public Task(String description) {
        this(description, TYPE_TODO);
    }

    /**
     * Sets the completion status of this task.
     *
     * @param isCompleted True if the task is completed, false otherwise
     */
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * Returns the description of this task.
     *
     * @return The task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a string representation of the task showing its description.
     * Subclasses may override to provide additional information.
     *
     * @return The task description
     */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return True if the task is completed, false otherwise
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Returns the type character of this task.
     *
     * @return The task type: T, D, or E
     */
    public char getTaskType() {
        return taskType;
    }
}

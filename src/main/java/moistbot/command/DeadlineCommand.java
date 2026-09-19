package moistbot.command;

import moistbot.task.Task;
import moistbot.task.TaskManager;

/**
 * Adds a task with a deadline to the task list.
 */
public final class DeadlineCommand extends AddCommand {
    private final String by;

    /**
     * Creates a deadline command for the supplied description and deadline.
     *
     * @param description The deadline task description
     * @param by The deadline in its user-entered form
     */
    public DeadlineCommand(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Adds the deadline task represented by this command.
     *
     * @param tasks The task list to modify
     * @return The deadline task that was added
     */
    @Override
    protected Task createTask(TaskManager tasks) {
        return tasks.addDeadline(getDescription(), by);
    }
}

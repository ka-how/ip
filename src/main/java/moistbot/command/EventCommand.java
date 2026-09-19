package moistbot.command;

import moistbot.task.Task;
import moistbot.task.TaskManager;

/**
 * Adds an event task with a start and end time to the task list.
 */
public final class EventCommand extends AddCommand {
    /**
     * Creates an event command for the supplied description and times.
     *
     * @param description The event task description
     * @param from The event start time
     * @param to The event end time
     */
    public EventCommand(String description, String from, String to) {
        super(CommandType.EVENT, description, from, to);
    }

    /**
     * Adds the event task represented by this command.
     *
     * @param tasks The task list to modify
     * @return The event task that was added
     */
    @Override
    protected Task createTask(TaskManager tasks) {
        return tasks.addEvent(getDescription(), getFrom(), getTo());
    }
}

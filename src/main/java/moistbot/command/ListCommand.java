package moistbot.command;

import moistbot.storage.Storage;
import moistbot.task.TaskManager;
import moistbot.ui.UserInterface;

/**
 * Displays the current task list without modifying it.
 */
public final class ListCommand extends Command {
    /**
     * Creates a list command without additional arguments.
     */
    public ListCommand() {
    }

    /**
     * Displays every task in its current order.
     *
     * @param tasks The task list to display
     * @param ui The user interface used to display the tasks
     * @param storage The persistence service, which is unused by this command
     */
    @Override
    public void execute(TaskManager tasks, UserInterface ui, Storage storage) {
        ui.printTasks(tasks);
    }
}

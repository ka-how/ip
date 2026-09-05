package moistbot.ui;

import moistbot.task.Task;
import moistbot.task.TaskManager;

/**
 * Handles all user interface output and formatting for the task manager application.
 * Provides methods to display welcome messages, task lists, and confirmations
 * with formatted output using visual dividers for better readability.
 */
public final class UserInterface {
    private static final String BANNER = " __  __   ___   ___ ____ _____ ____   ___ _____\n"
            + "|  \\/  | / _ \\ |_ _|/ ___|_   _| __ ) / _ \\|_   _|\n"
            + "| |\\/| || | | | | | \\___ \\ | | |  _ \\| | | | | |\n"
            + "| |  | || |_| | | |  ___) || | | |_) | |_| | | |\n"
            + "|_|  |_| \\___/ |___||____/ |_| |____/ \\___/  |_|";
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Prevents instantiation because all console output operations are stateless.
     */
    private UserInterface() {
    }

    /**
     * Displays the welcome message with the application banner and greeting.
     * Called when the application starts to introduce the user to MoistBot.
     */
    public static void printWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Good day. I am MoistBot, at your service.");
        System.out.println("How may I assist you today?");
        System.out.println(DIVIDER);
    }

    /**
     * Displays the exit message when the user quits the application.
     */
    public static void printExit() {
        printMessage("Thank you for using MoistBot. Have a pleasant day.");
    }

    /**
     * Displays a confirmation message when a task is successfully added to the list.
     * Shows the added task and the updated task count.
     *
     * @param task The task that was added
     * @param size The updated total number of tasks in the list
     */
    public static void printAddTask(Task task, int size) {
        String header = "Certainly. I have added this task:";
        printMessage(header + "\n" + formatModifyTask(task, size));
    }

    /**
     * Displays a confirmation message when a task is marked as complete.
     *
     * @param task The task that was marked as complete
     */
    public static void printMarkTask(Task task) {
        String header = "Certainly. I have marked this task as complete:";
        printMessage(header + "\n" + formatTaskDetails(task));
    }

    /**
     * Displays a confirmation message when a task is marked as incomplete.
     *
     * @param task The task that was marked as incomplete
     */
    public static void printUnmarkTask(Task task) {
        String header = "Certainly. I have marked this task as incomplete:";
        printMessage(header + "\n" + formatTaskDetails(task));
    }

    /**
     * Formats the output for task modification operations.
     * Combines the formatted task details with the updated task count.
     *
     * @param task The task that was modified
     * @param size The updated total number of tasks in the list
     * @return A formatted string with task details and task count
     */
    public static String formatModifyTask(Task task, int size) {
        String taskNoun = size == 1 ? "task" : "tasks";
        String ending = "Your list now contains " + size + " " + taskNoun + ".";
        return formatTaskDetails(task) + "\n" + ending;
    }

    /**
     * Displays a message surrounded by visual dividers for better readability.
     *
     * @param message The message to display
     */
    public static void printMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /**
     * Displays all tasks in the list with 1-based indexing.
     * Retrieves tasks individually from {@link TaskManager} so that the manager's
     * underlying storage remains encapsulated.
     */
    public static void printTasks() {
        System.out.println(DIVIDER);
        System.out.println("Certainly. Here is your task list:");

        int size = TaskManager.getSize();
        if (size == 0) {
            System.out.println("Your task list is presently empty. You may use: bye, list, todo, deadline, event, "
                    + "mark, or unmark.");
        }
        for (int i = 0; i < size; i++) {
            System.out.print((i + 1) + ".");
            System.out.println(formatTaskDetails(TaskManager.getTask(i + 1)));
        }

        System.out.println(DIVIDER);
    }

    /**
     * Formats task details for display.
     * Shows the task type in [T/D/E], completion status as [X] for complete or [ ] for incomplete,
     * followed by the task description (which may include additional time information for deadlines and events).
     *
     * @param task The task to format
     * @return A formatted string representation of the task
     */
    public static String formatTaskDetails(Task task) {
        char cross = task.isCompleted() ? 'X' : ' ';
        char taskType = task.getTaskType();
        return "[" + taskType + "][" + cross + "] " + task;
    }
}

/**
 * Handles all user interface output and formatting for the task manager application.
 * Provides methods to display welcome messages, task lists, and confirmations
 * with formatted output using visual dividers for better readability.
 */
public class UserInterface {
    private static final String BANNER = " __  __   ___   ___ ____ _____ ____   ___ _____\n"
            + "|  \\/  | / _ \\ |_ _|/ ___|_   _| __ ) / _ \\|_   _|\n"
            + "| |\\/| || | | | | | \\___ \\ | | |  _ \\| | | | | |\n"
            + "| |  | || |_| | | |  ___) || | | |_) | |_| | | |\n"
            + "|_|  |_| \\___/ |___||____/ |_| |____/ \\___/  |_|";
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Displays the welcome message with the application banner and greeting.
     * Called when the application starts to introduce the user to MoistBot.
     */
    public static void printWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm MoistBot");
        System.out.println("How can I help you today?");
        System.out.println(DIVIDER);
    }

    /**
     * Displays the exit message when the user quits the application.
     */
    public static void printExit() {
        printMessage("Bye! Have a nice day!");
    }

    /**
     * Displays a confirmation message when a task is successfully added to the list.
     * Shows the added task and the updated task count.
     *
     * @param task The task that was added
     * @param size The updated total number of tasks in the list
     */
    public static void printAddTask(Task task, int size) {
        String header = "Task added!";
        printMessage(header + "\n" + formatModifyTask(task, size));
    }

    /**
     * Displays a confirmation message when a task is marked as complete.
     * Shows the marked task and the updated task count.
     *
     * @param task The task that was marked as complete
     * @param size The total number of tasks in the list
     */
    public static void printMarkTask(Task task, int size) {
        String header = "Task marked as complete!";
        printMessage(header + "\n" + formatModifyTask(task, size));
    }

    /**
     * Displays a confirmation message when a task is marked as incomplete.
     * Shows the unmarked task and the updated task count.
     *
     * @param task The task that was marked as incomplete
     * @param size The total number of tasks in the list
     */
    public static void printUnmarkTask(Task task, int size) {
        String header = "Task marked as incomplete";
        printMessage(header + "\n" + formatModifyTask(task, size));
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
        String ending = "Now you have " + size + " tasks in the list";
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
     * Each task is displayed with its completion status and details.
     *
     * @param tasks The array containing tasks to display
     * @param size The number of tasks currently in the list
     */
    public static void printTasks(Task[] tasks, int size) {
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < size; i++) {
            System.out.print((i + 1) + ".");
            System.out.println(formatTaskDetails(tasks[i]));
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

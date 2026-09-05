import java.util.Scanner;

/**
 * MoistBot is a simple console-based task tracker application that teaches Java fundamentals.
 * The application accepts commands such as adding, listing, marking, and unmarking tasks.
 * Parsing and command handling are separated into helper classes (Parser, Command, TaskManager)
 * for clarity, maintainability, and easier testing.
 */
public class MoistBot {
    /**
     * Entry point of the MoistBot application.
     * Prints the startup message and delegates interactive command handling to the
     * execute(Command) method. The bye command prints the shutdown message before exiting.
     *
     * The Scanner is scoped to the application lifetime; closing it also closes System.in
     * when the application terminates.
     *
     * @param args Command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        UserInterface.printWelcome();

        boolean isExit = false;
        try (Scanner in = new Scanner(System.in)) {
            while (!isExit && in.hasNextLine()) {
                isExit = processCommand(in.nextLine());
            }
        }
    }

    /**
     * Parses and executes one user command, displaying any input error to the user.
     *
     * @param input the command entered by the user
     * @return whether the command requests the application to exit
     */
    private static boolean processCommand(String input) {
        try {
            Command command = Parser.parseInput(input);
            return execute(command);
        } catch (IllegalArgumentException e) {
            UserInterface.printMessage(e.getMessage());
            return false;
        }
    }

    /**
     * Executes a parsed command and prints the corresponding user feedback.
     * Handles all command types: BYE, LIST, MARK, UNMARK, TODO, DEADLINE, and EVENT.
     * Task operations delegate to TaskManager for manipulation and to UserInterface for display.
     *
     * @param command The parsed command object to execute
     * @return True if the application should exit, false otherwise
     */
    private static boolean execute(Command command) {
        Command.CommandType commandType = command.getCommandType();

        switch (commandType) {
            case BYE:
                return executeBye();
            case LIST:
                return executeList();
            case TODO:
                return executeTodo(command.getDescription());
            case DEADLINE:
                return executeDeadline(command.getDescription(), command.getTo());
            case EVENT:
                return executeEvent(command.getDescription(), command.getFrom(), command.getTo());
            case MARK:
                return executeMark(command.getDescription());
            case UNMARK:
                return executeUnmark(command.getDescription());
            default:
                throw new IllegalArgumentException("Unsupported command");
        }
    }

    /**
     * Executes the bye command to exit the application.
     *
     * @return Always returns true to signal application exit
     */
    private static boolean executeBye() {
        UserInterface.printExit();
        return true;
    }

    /**
     * Executes the list command to display all tasks.
     *
     * @return Always returns false to continue execution
     */
    private static boolean executeList() {
        UserInterface.printTasks();
        return false;
    }

    /**
     * Executes the todo command to add a new todo task.
     *
     * @param description The description of the todo task
     * @return Always returns false to continue execution
     */
    private static boolean executeTodo(String description) {
        return executeAddTask(TaskManager.addTodo(description));
    }

    /**
     * Executes the deadline command to add a new deadline task.
     *
     * @param description The description of the deadline task
     * @param by The deadline for the task
     * @return Always returns false to continue execution
     */
    private static boolean executeDeadline(String description, String by) {
        return executeAddTask(TaskManager.addDeadline(description, by));
    }

    /**
     * Executes the event command to add a new event task.
     *
     * @param description The description of the event
     * @param from The start time of the event
     * @param to The end time of the event
     * @return Always returns false to continue execution
     */
    private static boolean executeEvent(String description, String from, String to) {
        return executeAddTask(TaskManager.addEvent(description, from, to));
    }

    /**
     * Displays feedback after a task is added.
     *
     * @param task The added task
     * @return Always returns false to continue execution
     */
    private static boolean executeAddTask(Task task) {
        UserInterface.printAddTask(task, TaskManager.getSize());
        return false;
    }

    /**
     * Executes the mark command to mark a task as completed.
     *
     * @param description The 1-based index of the task to mark
     * @return Always returns false to continue execution
     * @throws IllegalArgumentException if the task index is invalid
     */
    private static boolean executeMark(String description) {
        int markIndex = Integer.parseInt(description);
        Task task = TaskManager.getTask(markIndex);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        task.setCompleted(true);
        UserInterface.printMarkTask(task);
        return false;
    }

    /**
     * Executes the unmark command to mark a task as incomplete.
     *
     * @param description The 1-based index of the task to unmark
     * @return Always returns false to continue execution
     * @throws IllegalArgumentException if the task index is invalid
     */
    private static boolean executeUnmark(String description) {
        int unmarkIndex = Integer.parseInt(description);
        Task task = TaskManager.getTask(unmarkIndex);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        task.setCompleted(false);
        UserInterface.printUnmarkTask(task);
        return false;
    }
}

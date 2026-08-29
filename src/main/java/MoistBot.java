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
     * Prints startup and shutdown messages and delegates interactive command handling to the
     * execute(Command) method so responsibilities remain separated for easier testing and maintenance.
     *
     * The Scanner is created and closed here to avoid leaving System.in closed
     * unexpectedly by other parts of the program.
     *
     * @param args Command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        UserInterface.printWelcome();

        boolean exit = false;
        try (Scanner in = new Scanner(System.in)) {
            while (!exit && in.hasNextLine()) {
                try {
                    Command command = Parser.parseInput(in.nextLine());
                    exit = execute(command);
                } catch (IllegalArgumentException e) {
                    UserInterface.printMessage(e.getMessage());
                }
            }
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
        String description = command.getDescription();
        String from = command.getFrom();
        String to = command.getTo();

        Task task;
        switch (commandType) {
            case BYE:
                UserInterface.printExit();
                return true;
            case LIST:
                UserInterface.printTasks(TaskManager.getTaskArray(), TaskManager.getSize());
                return false;
            case TODO:
                TaskManager.addTask(description);
                task = TaskManager.getTask(TaskManager.getSize());
                UserInterface.printAddTask(task, TaskManager.getSize());
                return false;
            case DEADLINE:
                TaskManager.addDeadline(description, to);
                task = TaskManager.getTask(TaskManager.getSize());
                UserInterface.printAddTask(task, TaskManager.getSize());
                return false;
            case EVENT:
                TaskManager.addEvent(description, from, to);
                task = TaskManager.getTask(TaskManager.getSize());
                UserInterface.printAddTask(task, TaskManager.getSize());
                return false;
            case MARK:
                int markIndex = Integer.parseInt(description);
                task = TaskManager.getTask(markIndex);
                if (task == null) {
                    throw new IllegalArgumentException("Task not found");
                }
                task.setCompleted(true);
                UserInterface.printMarkTask(task, TaskManager.getSize());
                return false;
            case UNMARK:
                int unmarkIndex = Integer.parseInt(description);
                task = TaskManager.getTask(unmarkIndex);
                if (task == null) {
                    throw new IllegalArgumentException("Task not found");
                }
                task.setCompleted(false);
                UserInterface.printUnmarkTask(task, TaskManager.getSize());
                return false;
            default:
                break;
        }

        return true;
    }
}

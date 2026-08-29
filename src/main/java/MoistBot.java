import java.util.Scanner;

/**
 * Runs a simple console-based task tracker used to teach Java fundamentals.
 * The application accepts commands such as adding, listing, marking, and
 * unmarking tasks, with parsing and command handling separated into helper
 * classes for clarity and maintainability.
 */
public class MoistBot {
    /**
     * Entry point that prints startup and shutdown messages and delegates
     * interactive command handling to parseInput(Scanner) so responsibilities
     * remain separated for easier testing and maintenance.
     *
     * The Scanner is created and closed here to avoid leaving System.in closed
     * unexpectedly by other callers.
     *
     * @param args Command-line arguments (not used)
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
     *
     * @param command The parsed command object to execute
     * @return True if the application should exit, false otherwise
     */
    private static boolean execute(Command command) {
        String commandType = command.getCommandType();
        String argument = command.getArgument();

        switch (commandType) {
            case "bye":
                UserInterface.printExit();
                return true;
            case "list":
                UserInterface.printTasks(TaskManager.getTaskArray(), TaskManager.getSize());
                return false;
            case "add":
                TaskManager.addTask(argument);
                UserInterface.printAddTask(argument);
                return false;
        }

        int taskIndex = Integer.parseInt(argument);
        Task task = TaskManager.getTask(taskIndex);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        switch (commandType) {
            case "mark":
                task.setCompleted(true);
                UserInterface.printMarkTask(task);
                return false;
            case "unmark":
                task.setCompleted(false);
                UserInterface.printUnmarkTask(task);
                return false;
        }

        return true;
    }
}

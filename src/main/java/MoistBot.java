import java.util.Scanner;

/**
 * Runs a simple console-based task tracker used to teach Java fundamentals.
 * The application accepts commands such as adding, listing, marking, and
 * unmarking tasks, with parsing and command handling separated into helper
 * classes for clarity and maintainability.
 */
public class MoistBot {
    private static final String BANNER = " __  __   ___   ___ ____ _____ ____   ___ _____\n"
            + "|  \\/  | / _ \\ |_ _|/ ___|_   _| __ ) / _ \\|_   _|\n"
            + "| |\\/| || | | | | | \\___ \\ | | |  _ \\| | | | | |\n"
            + "| |  | || |_| | | |  ___) || | | |_) | |_| | | |\n"
            + "|_|  |_| \\___/ |___||____/ |_| |____/ \\___/  |_|";
    private static final String DIVIDER = "____________________________________________________________";

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
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm MoistBot.");
        System.out.println("How can I help you today?");
        System.out.println(DIVIDER);

        boolean exit = false;
        try (Scanner in = new Scanner(System.in)) {
            while (!exit && in.hasNextLine()) {
                Command command = Parser.parseInput(in.nextLine());
                System.out.println(DIVIDER);
                exit = execute(command);
                System.out.println(DIVIDER);
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

        // Handle errors first
        if (commandType.equals("error")) {
            switch (argument) {
                case "noInput":
                    System.out.println("Please provide an input.");
                    return false;
                case "missingArgument":
                    System.out.println("Missing argument.");
                    return false;
                case "numberFormatException":
                    System.out.println("Please provide an integer argument.");
                    return false;
            }
        }

        switch (commandType) {
            case "bye":
                System.out.println("Bye! Have a nice day.");
                return true;
            case "list":
                System.out.println("Here are the tasks in your list:");
                TaskManager.printTasks();
                return false;
            case "add":
                TaskManager.addTask(argument);
                return false;
        }

        int taskIndex = Integer.parseInt(argument);
        Task task = TaskManager.getTask(taskIndex);
        if (task == null) {
            System.out.println("Task not found.");
            return false;
        }
        switch (commandType) {
            case "mark":
                System.out.println("Marking task " + taskIndex + ".");
                task.setCompleted(true);
                return false;
            case "unmark":
                System.out.println("Unmarking task " + taskIndex + ".");
                task.setCompleted(false);
                return false;
        }

        return true;
    }
}

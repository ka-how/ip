import java.util.Scanner;

/**
 * MoistBot provides a minimal, in-memory task manager used for teaching
 * introductory Java and object-oriented design. It exists to demonstrate
 * console I/O, simple command parsing, and interaction between small
 * collaborating classes (e.g., List and Task).
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

        try (Scanner in = new Scanner(System.in)) {
            parseInput(in);
        }

    }

    /**
     * Runs the main user interaction loop. Reads commands from the provided
     * Scanner and updates the task list via the List/Task classes. The caller
     * is responsible for creating and closing the Scanner (see main()).
     */
    private static void parseInput(Scanner in) {
        while (in.hasNextLine()) {
            String inputString = in.nextLine().trim();
            if (inputString.isEmpty()) {
                continue;
            }

            String[] inputArray = inputString.split(" ", 2);

            System.out.println(DIVIDER);

            String command = inputArray[0];

            if (command.equals("bye")) {
                System.out.println("Bye! Have a nice day.");
                System.out.println(DIVIDER);
                break;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                List.printTasks();
            } else if (command.equals("mark") || command.equals("unmark")) {
                try {
                    if (inputArray.length < 2) {
                        System.out.println("Error: Please provide a task number to " + command + ".");
                    } else {
                        int taskIndex = Integer.parseInt(inputArray[1]);
                        Task task = List.getTask(taskIndex);
                        if (task != null) {
                            boolean isMarking = command.equals("mark");
                            task.setCompleted(isMarking);
                            if (isMarking) {
                                System.out.println("Great! I've marked this task as completed:");
                            } else {
                                System.out.println("Task has been marked incomplete:");
                            }
                            System.out.print("  ");
                            task.printDetails();
                            System.out.println();
                        } else {
                            System.out.println("Error: Task number " + taskIndex + " not found.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid task number.");
                }
            } else {
                List.addTask(inputString);
            }

            System.out.println(DIVIDER);
        }
    }
}

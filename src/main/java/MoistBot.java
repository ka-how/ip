import java.util.Scanner;

/**
 * MoistBot is a task management chatbot that allows users to store and retrieve tasks.
 * Users can add tasks by entering text, view all stored tasks with the "list" command,
 * and exit the application by typing "bye". Tasks are stored in memory and not persisted.
 */
public class MoistBot {
    private static final String BANNER = " __  __   ___   ___ ____ _____ ____   ___ _____\n"
            + "|  \\/  | / _ \\ |_ _|/ ___|_   _| __ ) / _ \\|_   _|\n"
            + "| |\\/| || | | | | | \\___ \\ | | |  _ \\| | | | | |\n"
            + "| |  | || |_| | | |  ___) || | | |_) | |_| | | |\n"
            + "|_|  |_| \\___/ |___||____/ |_| |____/ \\___/  |_|";
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * The main entry point for the MoistBot application.
     * Displays a welcome banner and goodbye message, with task interaction handled
     * by the parseInput() method.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm MoistBot.");
        System.out.println("How can I help you today?");
        System.out.println(DIVIDER);

        parseInput();

        System.out.println(DIVIDER);
        System.out.println("Bye! Have a nice day.");
        System.out.println(DIVIDER);
    }

    /**
     * Reads user input in an interactive loop and processes commands.
     * Handles three types of input:
     * - "list": Displays all stored tasks
     * - "bye": Exits the loop
     * - Any other text: Adds the text as a new task
     * The loop continues until the user enters "bye".
     */
    private static void parseInput() {
        Scanner in = new Scanner(System.in);
        String inputString = in.nextLine();

        while (!inputString.equals("bye")) {
            System.out.println(DIVIDER);
            if (inputString.equals("list")) {
                List.printText();
            }
            else {
                List.addText(inputString);
            }
            System.out.println(DIVIDER);
            inputString = in.nextLine();
        }
    }
}

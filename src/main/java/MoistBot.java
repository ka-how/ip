import java.util.Scanner;

/**
 * MoistBot is a simple command-line chatbot that greets users and echoes their input
 * until they enter the "bye" command to exit.
 */
public class MoistBot {
    /**
     * The main entry point for the MoistBot application.
     * Displays a welcome banner and starts an interactive loop that reads user input
     * and echoes it back until the user types "bye" to exit.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        String banner = " __  __   ___   ___ ____ _____ ____   ___ _____\n"
                + "|  \\/  | / _ \\ |_ _|/ ___|_   _| __ ) / _ \\|_   _|\n"
                + "| |\\/| || | | | | | \\___ \\ | | |  _ \\| | | | | |\n"
                + "| |  | || |_| | | |  ___) || | | |_) | |_| | | |\n"
                + "|_|  |_| \\___/ |___||____/ |_| |____/ \\___/  |_|";
        String divider = "____________________________________________________________";

        System.out.println(divider);
        System.out.println(banner);
        System.out.println("Hello! I'm MoistBot.");
        System.out.println("How can I help you today?");
        System.out.println(divider);

        Scanner in = new Scanner(System.in);
        while (true) {
            String input = in.nextLine();
            if (input.equals("bye")) {
                break;
            }

            System.out.println(divider);
            System.out.println(input);
            System.out.println(divider);
        }

        System.out.println(divider);
        System.out.println("Bye!");
        System.out.println(divider);
    }
}

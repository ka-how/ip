/**
 * Starts MoistBot by displaying a greeting and farewell message.
 */
public class MoistBot {
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
        System.out.println("Bye!");
        System.out.println(divider);
    }
}

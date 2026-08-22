/**
 * Manages a static list of tasks stored in memory.
 * Provides functionality to add tasks and display all stored tasks.
 * The list can store up to 100 tasks using a fixed-size array.
 */
public class List {
    private static final String[] TEXT_ARRAY = new String[100];
    private static int size = 0;

    /**
     * Adds a new task to the list and displays a confirmation message.
     *
     * @param text The task text to add to the list
     */
    public static void addText(String text) {
        if (size == TEXT_ARRAY.length) {
            System.out.println("Memory full!");
            return;
        }
        TEXT_ARRAY[size] = text;
        size++;
        System.out.println("Added: " + text);
    }

    /**
     * Displays all tasks in the list with their index numbers (1-based).
     * If the list is empty, no output is displayed.
     */
    public static void printText() {
        for (int i = 0; i < size; i++) {
            System.out.println((i + 1) + ": " + TEXT_ARRAY[i]);
        }
    }
}

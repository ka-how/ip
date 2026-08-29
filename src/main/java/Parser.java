/**
 * Parses raw user input into a {@link Command} object understood by MoistBot.
 * The parser normalises whitespace and validates required arguments before
 * constructing the corresponding command object.
 */
public class Parser {
    /**
     * Parses a user-entered line into a command object.
     *
     * @param inputString The raw text entered by the user
     * @return A parsed command object
     * @throws IllegalArgumentException if the input is blank or malformed
     */
    public static Command parseInput(String inputString) {
        if (inputString == null) {
            throw new IllegalArgumentException("Please provide an input");
        }

        String trimmedInput = inputString.trim();
        if (trimmedInput.isEmpty()) {
            throw new IllegalArgumentException("Please provide an input");
        }

        String[] inputArray = trimmedInput.split("\\s+", 2);
        String commandType = inputArray[0];
        switch (commandType) {
            case "bye":
            case "list":
                return new Command(commandType, null);
            case "mark":
            case "unmark":
                if (inputArray.length < 2) {
                    throw new IllegalArgumentException("Missing argument");
                }
                try {
                    Integer.parseInt(inputArray[1]);
                    return new Command(commandType, inputArray[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Please provide an integer argument", e);
                }
            default:
                return new Command("add", trimmedInput);
        }
    }
}
/**
 * Parses raw user input into a {@link Command} object understood by MoistBot.
 * The parser normalises whitespace, validates required arguments, and converts
 * malformed or empty input into explicit error commands.
 */
public class Parser {
    /**
     * Parses a user-entered line into a command object.
     *
     * @param inputString The raw text entered by the user
     * @return A parsed command object, including an error command when input is
     *         invalid or incomplete
     */
    public static Command parseInput(String inputString) {
        inputString = inputString.trim();
        if (inputString.isEmpty()) {
            return new Command("error", "noInput");
        }

        String[] inputArray = inputString.split("\\s+", 2);
        String commandType = inputArray[0];
        switch (commandType) {
            case "bye":
            case "list":
                return new Command(commandType, null);
            case "mark":
            case "unmark":
                if (inputArray.length < 2) {
                    return new Command("error", "missingArgument");
                }
                try {
                    Integer.parseInt(inputArray[1]);
                    return new Command(commandType, inputArray[1]);
                } catch (NumberFormatException e) {
                    return new Command("error", "numberFormatException");
                }
        }

        return new Command("add", inputString);
    }
}
/**
 * Parses raw user input into a {@link Command} object understood by MoistBot.
 * The parser normalizes whitespace, identifies command keywords, and validates
 * required arguments before constructing the corresponding command object.
 */
public class Parser {
    private static final String DEADLINE_USAGE = "deadline <desc> /by <time>";
    private static final String EVENT_USAGE = "event <desc> /from <time> /to <time>";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";
    private static final String INPUT_ERROR = "Please provide an input";
    private static final String DESC_MISSING = "Description missing. Usage: ";
    private static final String ARG_MISSING = "Missing argument";
    private static final String UNKNOWN_COMMAND = "Unknown command: ";
    /**
     * Parses a user-entered line into a command object.
     * Recognizes command keywords (bye, list, mark, unmark, todo, deadline, event)
     * and delegates to specialized parsing methods for complex commands.
     *
     * @param inputString The raw text entered by the user
     * @return A parsed command object
     * @throws IllegalArgumentException if the input is blank, has an unknown command,
     *                                  is missing required arguments, or is malformed
     */
    public static Command parseInput(String inputString) {
        String trimmedInput = validateInput(inputString);
        String[] inputArray = trimmedInput.split("\\s+", 2);
        String commandText = inputArray[0];

        return parseCommand(commandText, inputArray);
    }

    /**
     * Validates that the input string is not null or empty.
     *
     * @param inputString The raw text to validate
     * @return The trimmed input string
     * @throws IllegalArgumentException if the input is null or blank
     */
    private static String validateInput(String inputString) {
        if (inputString == null) {
            throw new IllegalArgumentException(INPUT_ERROR);
        }

        String trimmedInput = inputString.trim();
        if (trimmedInput.isEmpty()) {
            throw new IllegalArgumentException(INPUT_ERROR);
        }

        return trimmedInput;
    }

    /**
     * Parses a command based on its keyword and arguments.
     *
     * @param commandText The command keyword
     * @param inputArray The array containing the command and its arguments
     * @return A parsed command object
     * @throws IllegalArgumentException if the command arguments are invalid
     */
    private static Command parseCommand(String commandText, String[] inputArray) {
        switch (commandText) {
            case "bye":
                return new Command(Command.CommandType.BYE, null);
            case "list":
                return new Command(Command.CommandType.LIST, null);
            case "mark":
                return parseMarkOrUnmark(Command.CommandType.MARK, inputArray);
            case "unmark":
                return parseMarkOrUnmark(Command.CommandType.UNMARK, inputArray);
            case "todo":
                return parseTodo(inputArray);
            case "deadline":
                return parseDeadlineCommand(inputArray);
            case "event":
                return parseEventCommand(inputArray);
            default:
                throw new IllegalArgumentException(UNKNOWN_COMMAND + commandText);
        }
    }

    /**
     * Parses mark or unmark commands that require an integer argument.
     *
     * @param commandType The command type (MARK or UNMARK)
     * @param inputArray The array containing the command and its arguments
     * @return A parsed MARK or UNMARK command
     * @throws IllegalArgumentException if the argument is missing or not an integer
     */
    private static Command parseMarkOrUnmark(Command.CommandType commandType, String[] inputArray) {
        if (inputArray.length < 2) {
            throw new IllegalArgumentException(ARG_MISSING);
        }
        try {
            Integer.parseInt(inputArray[1]);
            return new Command(commandType, inputArray[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please provide an integer argument", e);
        }
    }

    /**
     * Parses a todo command.
     *
     * @param inputArray The array containing the command and its arguments
     * @return A parsed TODO command
     * @throws IllegalArgumentException if the description is missing
     */
    private static Command parseTodo(String[] inputArray) {
        if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
            throw new IllegalArgumentException(DESC_MISSING + "todo <description>");
        }
        return new Command(Command.CommandType.TODO, inputArray[1].trim());
    }

    /**
     * Parses a deadline command.
     *
     * @param inputArray The array containing the command and its arguments
     * @return A parsed DEADLINE command
     * @throws IllegalArgumentException if the description is missing
     */
    private static Command parseDeadlineCommand(String[] inputArray) {
        if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
            throw new IllegalArgumentException(DESC_MISSING + DEADLINE_USAGE);
        }
        return parseDeadline(inputArray[1]);
    }

    /**
     * Parses an event command.
     *
     * @param inputArray The array containing the command and its arguments
     * @return A parsed EVENT command
     * @throws IllegalArgumentException if the description is missing
     */
    private static Command parseEventCommand(String[] inputArray) {
        if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
            throw new IllegalArgumentException(DESC_MISSING + EVENT_USAGE);
        }
        return parseEvent(inputArray[1]);
    }


    /**
     * Parses a deadline command string in the format "description /by deadline".
     *
     * @param inputString The deadline command arguments (without the "deadline" keyword)
     * @return A parsed DEADLINE command with description and deadline
     * @throws IllegalArgumentException if the format is invalid or required fields are missing
     */
    public static Command parseDeadline(String inputString) {
        if (!inputString.contains(DEADLINE_SEPARATOR)) {
            throw new IllegalArgumentException("Invalid command. Usage: " + DEADLINE_USAGE);
        }

        String[] inputArray = inputString.split(DEADLINE_SEPARATOR, 2);
        String description = inputArray[0].trim();
        String by = inputArray[1].trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException(DESC_MISSING + DEADLINE_USAGE);
        }
        if (by.isEmpty()) {
            throw new IllegalArgumentException("Deadline missing. Usage: " + DEADLINE_USAGE);
        }

        return new Command(Command.CommandType.DEADLINE, description, null, by);
    }

    /**
     * Parses an event command string in the format "description /from start /to end".
     *
     * @param inputString The event command arguments (without the "event" keyword)
     * @return A parsed EVENT command with description, start time, and end time
     * @throws IllegalArgumentException if the format is invalid or required fields are missing
     */
    public static Command parseEvent(String inputString) {
        if (!(inputString.contains(EVENT_FROM_SEPARATOR) && inputString.contains(EVENT_TO_SEPARATOR))) {
            throw new IllegalArgumentException("Invalid command. Usage: " + EVENT_USAGE);
        }

        String[] inputArray = inputString.split(EVENT_FROM_SEPARATOR + "|" + EVENT_TO_SEPARATOR, 3);
        String description = inputArray[0].trim();
        String from = inputArray[1].trim();
        String to = inputArray[2].trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException(DESC_MISSING + EVENT_USAGE);
        }
        if (from.isEmpty()) {
            throw new IllegalArgumentException("Time missing. Usage: " + EVENT_USAGE);
        }
        if (to.isEmpty()) {
            throw new IllegalArgumentException("Time missing. Usage: " + EVENT_USAGE);
        }

        return new Command(Command.CommandType.EVENT, description, from, to);
    }
}

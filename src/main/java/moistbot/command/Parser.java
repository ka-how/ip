package moistbot.command;

import moistbot.exception.MoistBotException;

/**
 * Parses raw user input into a {@link Command} object understood by MoistBot.
 * The parser normalizes whitespace, identifies command keywords, and validates
 * required arguments before constructing the corresponding command object.
 */
public final class Parser {
    private static final String DEADLINE_USAGE = "deadline <desc> /by <time>";
    private static final String EVENT_USAGE = "event <desc> /from <time> /to <time>";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";
    private static final String INPUT_ERROR = "Please enter a command, such as 'list' or 'todo buy milk'.";
    private static final String UNKNOWN_COMMAND = "My apologies, but I do not recognise the command '%s'. "
            + "Available commands are: bye, list, todo, deadline, event, mark, and unmark.";

    /**
     * Prevents instantiation because parsing operations do not require object state.
     */
    private Parser() {
    }

    /**
     * Parses a user-entered line into a command object.
     * Recognizes command keywords (bye, list, mark, unmark, todo, deadline, event)
     * and delegates to specialized parsing methods for complex commands.
     *
     * @param inputString The raw text entered by the user
     * @return A parsed command object
     * @throws MoistBotException if the input is blank, has an unknown command,
     *                                  is missing required arguments, or is malformed
     */
    public static Command parseInput(String inputString) throws MoistBotException {
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
     * @throws MoistBotException if the input is null or blank
     */
    private static String validateInput(String inputString) throws MoistBotException {
        if (inputString == null) {
            throw new MoistBotException(INPUT_ERROR);
        }

        String trimmedInput = inputString.trim();
        if (trimmedInput.isEmpty()) {
            throw new MoistBotException(INPUT_ERROR);
        }

        return trimmedInput;
    }

    /**
     * Parses a command based on its keyword and arguments.
     *
     * @param commandText The command keyword
     * @param inputArray The array containing the command and its arguments
     * @return A parsed command object
     * @throws MoistBotException if the command arguments are invalid
     */
    private static Command parseCommand(String commandText, String[] inputArray) throws MoistBotException {
        switch (commandText) {
            case "bye":
                return parseCommandWithoutArguments(Command.CommandType.BYE, inputArray);
            case "list":
                return parseCommandWithoutArguments(Command.CommandType.LIST, inputArray);
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
                throw new MoistBotException(String.format(UNKNOWN_COMMAND, commandText));
        }
    }

    /**
     * Parses a command that must not have arguments.
     *
     * @param commandType The command type to create
     * @param inputArray The array containing the command and any supplied text
     * @return A parsed command without arguments
     * @throws MoistBotException if unexpected text follows the command
     */
    private static Command parseCommandWithoutArguments(Command.CommandType commandType, String[] inputArray)
            throws MoistBotException {
        if (inputArray.length > 1) {
            String commandName = commandType.name().toLowerCase();
            throw new MoistBotException("The '" + commandName + "' command does not accept arguments. Please "
                    + "enter only '" + commandName + "'.");
        }
        return new Command(commandType, null);
    }

    /**
     * Parses mark or unmark commands that require an integer argument.
     *
     * @param commandType The command type (MARK or UNMARK)
     * @param inputArray The array containing the command and its arguments
     * @return A parsed MARK or UNMARK command
     * @throws MoistBotException if the argument is missing or not an integer
     */
    private static Command parseMarkOrUnmark(Command.CommandType commandType, String[] inputArray)
            throws MoistBotException {
        String commandName = commandType.name().toLowerCase();
        if (inputArray.length < 2) {
            throw new MoistBotException("Please provide a task number. Usage: " + commandName + " <task number>, "
                    + "for example '" + commandName + " 1'.");
        }
        try {
            Integer.parseInt(inputArray[1]);
            return new Command(commandType, inputArray[1]);
        } catch (NumberFormatException e) {
            throw new MoistBotException("My apologies, but '" + inputArray[1] + "' is not a valid task number. "
                    + "Please enter one whole number, for example '" + commandName + " 1'.");
        }
    }

    /**
     * Parses a todo command.
     *
     * @param inputArray The array containing the command and its arguments
     * @return A parsed TODO command
     * @throws MoistBotException if the description is missing
     */
    private static Command parseTodo(String[] inputArray) throws MoistBotException {
        if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
            throw new MoistBotException("Please provide a description for the todo task. Usage: todo <description>, "
                    + "for example 'todo buy milk'.");
        }
        return new Command(Command.CommandType.TODO, inputArray[1].trim());
    }

    /**
     * Parses a deadline command.
     *
     * @param inputArray The array containing the command and its arguments
     * @return A parsed DEADLINE command
     * @throws MoistBotException if the description is missing
     */
    private static Command parseDeadlineCommand(String[] inputArray) throws MoistBotException {
        if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
            throw new MoistBotException("Please provide a deadline description and time. Usage: " + DEADLINE_USAGE
                    + ", for example 'deadline return book /by Friday'.");
        }
        return parseDeadline(inputArray[1]);
    }

    /**
     * Parses an event command.
     *
     * @param inputArray The array containing the command and its arguments
     * @return A parsed EVENT command
     * @throws MoistBotException if the description is missing
     */
    private static Command parseEventCommand(String[] inputArray) throws MoistBotException {
        if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
            throw new MoistBotException("Please provide an event description and times. Usage: " + EVENT_USAGE
                    + ", for example 'event meeting /from 2pm /to 4pm'.");
        }
        return parseEvent(inputArray[1]);
    }


    /**
     * Parses a deadline command string in the format "description /by deadline".
     *
     * @param inputString The deadline command arguments (without the "deadline" keyword)
     * @return A parsed DEADLINE command with description and deadline
     * @throws MoistBotException if the format is invalid or required fields are missing
     */
    public static Command parseDeadline(String inputString) throws MoistBotException {
        int separatorIndex = findSeparatorIndex(inputString, DEADLINE_SEPARATOR, 0);
        if (separatorIndex < 0) {
            throw new MoistBotException("Please include the '/by' separator. Usage: " + DEADLINE_USAGE + ".");
        }
        if (findSeparatorIndex(inputString, DEADLINE_SEPARATOR,
                separatorIndex + DEADLINE_SEPARATOR.length()) >= 0) {
            throw new MoistBotException("A deadline may contain only one '/by' separator. Usage: "
                    + DEADLINE_USAGE + ".");
        }

        String description = inputString.substring(0, separatorIndex).trim();
        String by = inputString.substring(separatorIndex + DEADLINE_SEPARATOR.length()).trim();

        if (description.isEmpty()) {
            throw new MoistBotException("Please provide a deadline description before '/by'. Usage: "
                    + DEADLINE_USAGE + ".");
        }
        if (by.isEmpty()) {
            throw new MoistBotException("Please provide a deadline time after '/by'. Usage: " + DEADLINE_USAGE + ".");
        }

        return new Command(Command.CommandType.DEADLINE, description, null, by);
    }

    /**
     * Parses an event command string in the format "description /from start /to end".
     *
     * @param inputString The event command arguments (without the "event" keyword)
     * @return A parsed EVENT command with description, start time, and end time
     * @throws MoistBotException if the format is invalid or required fields are missing
     */
    public static Command parseEvent(String inputString) throws MoistBotException {
        int fromIndex = findSeparatorIndex(inputString, EVENT_FROM_SEPARATOR, 0);
        int toIndex = findSeparatorIndex(inputString, EVENT_TO_SEPARATOR, 0);
        if (fromIndex < 0) {
            throw new MoistBotException("Please include the '/from' separator. Usage: " + EVENT_USAGE + ".");
        }
        if (toIndex < 0) {
            throw new MoistBotException("Please include the '/to' separator. Usage: " + EVENT_USAGE + ".");
        }
        if (fromIndex > toIndex) {
            throw new MoistBotException("Please place '/from' before '/to'. Usage: " + EVENT_USAGE + ".");
        }
        if (findSeparatorIndex(inputString, EVENT_FROM_SEPARATOR,
                fromIndex + EVENT_FROM_SEPARATOR.length()) >= 0
                || findSeparatorIndex(inputString, EVENT_TO_SEPARATOR,
                toIndex + EVENT_TO_SEPARATOR.length()) >= 0) {
            throw new MoistBotException("An event must contain exactly one '/from' and one '/to' separator. "
                    + "Usage: " + EVENT_USAGE + ".");
        }

        String description = inputString.substring(0, fromIndex).trim();
        String from = inputString.substring(fromIndex + EVENT_FROM_SEPARATOR.length(), toIndex).trim();
        String to = inputString.substring(toIndex + EVENT_TO_SEPARATOR.length()).trim();

        if (description.isEmpty()) {
            throw new MoistBotException("Please provide an event description before '/from'. Usage: "
                    + EVENT_USAGE + ".");
        }
        if (from.isEmpty()) {
            throw new MoistBotException("Please provide an event start time after '/from'. Usage: "
                    + EVENT_USAGE + ".");
        }
        if (to.isEmpty()) {
            throw new MoistBotException("Please provide an event end time after '/to'. Usage: " + EVENT_USAGE + ".");
        }

        return new Command(Command.CommandType.EVENT, description, from, to);
    }

    /**
     * Finds a separator only when it appears as a distinct whitespace-delimited token.
     * This prevents text such as "/bypass" from being mistaken for the "/by" separator.
     *
     * @param inputString The command arguments to search
     * @param separator The separator token to find
     * @param startIndex The index at which to begin searching
     * @return The separator index, or -1 if no complete separator token exists
     */
    private static int findSeparatorIndex(String inputString, String separator, int startIndex) {
        int separatorIndex = inputString.indexOf(separator, startIndex);
        while (separatorIndex >= 0) {
            int afterSeparator = separatorIndex + separator.length();
            boolean hasValidStart = separatorIndex == 0
                    || Character.isWhitespace(inputString.charAt(separatorIndex - 1));
            boolean hasValidEnd = afterSeparator == inputString.length()
                    || Character.isWhitespace(inputString.charAt(afterSeparator));
            if (hasValidStart && hasValidEnd) {
                return separatorIndex;
            }
            separatorIndex = inputString.indexOf(separator, separatorIndex + separator.length());
        }
        return -1;
    }
}

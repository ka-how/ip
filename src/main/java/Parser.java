/**
 * Parses raw user input into a {@link Command} object understood by MoistBot.
 * The parser normalizes whitespace, identifies command keywords, and validates
 * required arguments before constructing the corresponding command object.
 */
public class Parser {
    /**
     * Parses a user-entered line into a command object.
     * Recognizes command keywords (bye, list, mark, unmark, todo, deadline, event)
     * and delegates to specialized parsing methods for complex commands.
     * Unrecognized commands default to todo.
     *
     * @param inputString The raw text entered by the user
     * @return A parsed command object
     * @throws IllegalArgumentException if the input is blank, missing required arguments, or malformed
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
        String commandText = inputArray[0];
        Command.CommandType commandType;
        switch (commandText) {
            case "bye":
                commandType = Command.CommandType.BYE;
                return new Command(commandType, null);
            case "list":
                commandType = Command.CommandType.LIST;
                return new Command(commandType, null);
            case "mark":
                if (inputArray.length < 2) {
                    throw new IllegalArgumentException("Missing argument");
                }
                try {
                    Integer.parseInt(inputArray[1]);
                    return new Command(Command.CommandType.MARK, inputArray[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Please provide an integer argument", e);
                }
            case "unmark":
                if (inputArray.length < 2) {
                    throw new IllegalArgumentException("Missing argument");
                }
                try {
                    Integer.parseInt(inputArray[1]);
                    return new Command(Command.CommandType.UNMARK, inputArray[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Please provide an integer argument", e);
                }
            case "todo":
                if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
                    throw new IllegalArgumentException("Description missing. Usage: todo <description>");
                }
                return new Command(Command.CommandType.TODO, inputArray[1].trim());
            case "deadline":
                if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
                    throw new IllegalArgumentException("Description missing. Usage: deadline <desc> /by <time>");
                }
                return parseDeadline(inputArray[1]);
            case "event":
                if (inputArray.length < 2 || inputArray[1].trim().isEmpty()) {
                    throw new IllegalArgumentException("Description missing. Usage: event <desc> /from <time> /to <time>");
                }
                return parseEvent(inputArray[1]);
            default:
                return new Command(Command.CommandType.TODO, trimmedInput);
        }
    }

    /**
     * Parses a deadline command string in the format "description /by deadline".
     *
     * @param inputString The deadline command arguments (without the "deadline" keyword)
     * @return A parsed DEADLINE command with description and deadline
     * @throws IllegalArgumentException if the format is invalid or required fields are missing
     */
    public static Command parseDeadline(String inputString) {
        if (!inputString.contains(" /by ")) {
            throw new IllegalArgumentException("Invalid command. Usage: deadline <desc> /by <time>");
        }

        String[] inputArray = inputString.split(" /by ", 2);
        String description = inputArray[0].trim();
        String by = inputArray[1].trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description missing. Usage: deadline <desc> /by <time>");
        }
        if (by.isEmpty()) {
            throw new IllegalArgumentException("Deadline missing. Usage: deadline <desc> /by <time>");
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
        if (!(inputString.contains(" /from ") && inputString.contains(" /to "))) {
            throw new IllegalArgumentException("Invalid command. Usage: event <desc> /from <time> /to <time>");
        }

        String[] inputArray = inputString.split(" /from | /to ", 3);
        String description = inputArray[0].trim();
        String from = inputArray[1].trim();
        String to = inputArray[2].trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description missing. Usage: event <desc> /from <time> /to <time>");
        }
        if (from.isEmpty()) {
            throw new IllegalArgumentException("Time missing. Usage: event <desc> /from <time> /to <time>");
        }
        if (to.isEmpty()) {
            throw new IllegalArgumentException("Time missing. usage: event <desc> /from <time> /to <time>");
        }

        return new Command(Command.CommandType.EVENT, description, from, to);
    }
}
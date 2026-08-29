/**
 * Represents a user command parsed from console input.
 * The command type identifies the action to perform, while optional string fields
 * carry any extra data required by that action (e.g., description, time range).
 */
public class Command {
    /**
     * Enumeration of possible command types.
     * BYE: exit the application
     * LIST: display all tasks
     * MARK: mark a task as completed
     * UNMARK: mark a task as incomplete
     * TODO: add a simple todo item
     * DEADLINE: add a task with a deadline
     * EVENT: add a task with a time range
     */
    public enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        TODO,
        DEADLINE,
        EVENT
    }

    private final CommandType commandType;
    private final String description;
    private final String from;
    private final String to;

    /**
     * Constructs a command with the given type and all optional arguments.
     *
     * @param commandType The type of command to create
     * @param description The task description or task index argument
     * @param from The start time for event commands, or null for other types
     * @param to The end time for event/deadline commands, or null for other types
     */
    public Command(CommandType commandType, String description, String from, String to) {
        this.commandType = commandType;
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Constructs a command with the given type and description.
     * The from and to fields are initialized to null.
     *
     * @param commandType The type of command to create
     * @param description The task description or task index argument
     */
    public Command(CommandType commandType, String description) {
        this(commandType, description, null, null);
    }

    /**
     * Returns the command type.
     *
     * @return The command type enum, or null if the command is empty
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Returns the command description.
     * For TODO/DEADLINE/EVENT commands, this is the task description.
     * For MARK/UNMARK commands, this is the 1-based task index.
     *
     * @return The description or index provided with the command, or null if absent
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the start time for event/deadline commands.
     *
     * @return The start time, or null if not applicable
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time for event/deadline commands.
     *
     * @return The end time, or null if not applicable
     */
    public String getTo() {
        return to;
    }
}

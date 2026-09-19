package moistbot.command;

import moistbot.exception.MoistBotException;
import moistbot.storage.Storage;
import moistbot.task.TaskManager;
import moistbot.ui.UserInterface;

/**
 * Represents a user command parsed from console input.
 * The command type identifies the action to perform, while optional string fields
 * carry any extra data required by that action (e.g., description, time range).
 */
public class Command {
    /**
     * Identifies an action that MoistBot can perform.
     */
    public enum CommandType {
        /** Exits the application. */
        BYE,
        /** Displays all tasks. */
        LIST,
        /** Marks a task as completed. */
        MARK,
        /** Marks a task as incomplete. */
        UNMARK,
        /** Deletes a task. */
        DELETE,
        /** Adds a basic todo task. */
        TODO,
        /** Adds a task with a deadline. */
        DEADLINE,
        /** Adds a task with a time range. */
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
     * @return the command type
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Returns the command description.
     * For TODO/DEADLINE/EVENT commands, this is the task description.
     * For MARK/UNMARK/DELETE commands, this is the 1-based task index.
     *
     * @return the description or index provided with the command, or {@code null} if absent
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the start time for an event command.
     *
     * @return the start time, or {@code null} if not applicable
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the deadline time or event end time.
     *
     * @return the deadline or end time, or {@code null} if not applicable
     */
    public String getTo() {
        return to;
    }

    /**
     * Executes this command using the application's collaborators.
     * Concrete command classes override this method as their behavior is extracted.
     *
     * @param tasks The task list to query or modify
     * @param ui The user interface used for feedback
     * @param storage The persistence service used after task changes
     * @throws MoistBotException if the command cannot be executed
     */
    public void execute(TaskManager tasks, UserInterface ui, Storage storage) throws MoistBotException {
        throw new MoistBotException("My apologies, but that command is not supported.");
    }

    /**
     * Returns whether executing this command should end the application.
     *
     * @return True if this command exits the application
     */
    public boolean isExit() {
        return false;
    }
}

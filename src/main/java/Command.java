/**
 * Represents a user command parsed from console input.
 * The command type identifies the action to perform, while the optional
 * argument carries any extra data required by that action.
 */
public class Command {
    private String commandType;
    private String argument;

    /**
     * Creates a command with the given type and argument.
     *
     * @param commandType The action to perform, such as "add" or "mark"
     * @param argument The optional value associated with the command
     */
    public Command(String commandType, String argument) {
        this.commandType = commandType;
        this.argument = argument;
    }

    /**
     * Creates an empty command that does not yet describe any action.
     */
    public Command() {
        this(null, null);
    }

    /**
     * Returns the command type.
     *
     * @return The command type string, or null if the command is empty
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns the command argument.
     *
     * @return The argument provided with the command, or null if absent
     */
    public String getArgument() {
        return argument;
    }
}

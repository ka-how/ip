package moistbot;

import moistbot.command.Command;
import moistbot.command.Parser;
import moistbot.exception.MoistBotException;
import moistbot.storage.Storage;
import moistbot.task.Task;
import moistbot.task.TaskManager;
import moistbot.ui.UserInterface;

import java.util.Scanner;

/**
 * MoistBot is a simple console-based task tracker application that teaches Java fundamentals.
 * The application accepts commands such as adding, listing, marking, unmarking, and deleting tasks.
 * Parsing and command handling are separated into helper classes (Parser, Command, TaskManager)
 * for clarity, maintainability, and easier testing.
 */
public final class MoistBot {
    /** The in-memory tasks owned by this application instance. */
    private final TaskManager taskManager;

    /**
     * Creates a MoistBot application with its own in-memory task list.
     */
    public MoistBot() {
        taskManager = new TaskManager();
    }

    /**
     * Entry point of the MoistBot application.
     *
     * @param args Command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        new MoistBot().run();
    }

    /**
     * Runs the interactive command loop until the user exits or the input stream closes.
     * The scanner is scoped to the application lifetime, so System.in is closed when the
     * application terminates.
     */
    public void run() {
        UserInterface.printWelcome();
        loadSavedTasks();

        boolean isExit = false;
        try (Scanner in = new Scanner(System.in)) {
            while (!isExit && in.hasNextLine()) {
                isExit = processCommand(in.nextLine());
            }
        }
    }

    /**
     * Loads saved tasks while allowing the application to remain usable if loading fails.
     */
    private void loadSavedTasks() {
        try {
            taskManager.setTasks(Storage.loadTasks());
        } catch (MoistBotException e) {
            UserInterface.printMessage(e.getMessage());
        }
    }

    /**
     * Parses and executes one user command, displaying any input error to the user.
     *
     * @param input the command entered by the user
     * @return whether the command requests the application to exit
     */
    private boolean processCommand(String input) {
        try {
            Command command = Parser.parseInput(input);
            return execute(command);
        } catch (MoistBotException e) {
            UserInterface.printMessage(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            UserInterface.printMessage("My apologies, but I could not process that command because of an unexpected "
                    + "internal error. Please check the command format and try again. If the matter persists, "
                    + "please restart MoistBot.");
            return false;
        }
    }

    /**
     * Executes a parsed command and prints the corresponding user feedback.
     * Handles all supported task and application commands.
     * Task operations delegate to TaskManager for manipulation and to UserInterface for display.
     *
     * @param command The parsed command object to execute
     * @return True if the application should exit, false otherwise
     */
    private boolean execute(Command command) throws MoistBotException {
        Command.CommandType commandType = command.getCommandType();

        switch (commandType) {
            case BYE:
                return executeBye();
            case LIST:
                return executeList();
            case TODO:
                return executeTodo(command.getDescription());
            case DEADLINE:
                return executeDeadline(command.getDescription(), command.getTo());
            case EVENT:
                return executeEvent(command.getDescription(), command.getFrom(), command.getTo());
            case MARK:
                return executeMark(command.getDescription());
            case UNMARK:
                return executeUnmark(command.getDescription());
            case DELETE:
                return executeDelete(command.getDescription());
            default:
                throw new MoistBotException("My apologies, but that command is not supported.");
        }
    }

    /**
     * Executes the bye command to exit the application.
     *
     * @return Always returns true to signal application exit
     */
    private static boolean executeBye() {
        UserInterface.printExit();
        return true;
    }

    /**
     * Executes the list command to display all tasks.
     *
     * @return Always returns false to continue execution
     */
    private boolean executeList() {
        UserInterface.printTasks(taskManager);
        return false;
    }

    /**
     * Executes the todo command to add a new todo task.
     *
     * @param description The description of the todo task
     * @return Always returns false to continue execution
     * @throws MoistBotException if the task cannot be added or saved
     */
    private boolean executeTodo(String description) throws MoistBotException {
        return executeAddTask(taskManager.addTodo(description));
    }

    /**
     * Executes the deadline command to add a new deadline task.
     *
     * @param description The description of the deadline task
     * @param by The deadline for the task
     * @return Always returns false to continue execution
     * @throws MoistBotException if the task cannot be added or saved
     */
    private boolean executeDeadline(String description, String by) throws MoistBotException {
        return executeAddTask(taskManager.addDeadline(description, by));
    }

    /**
     * Executes the event command to add a new event task.
     *
     * @param description The description of the event
     * @param from The start time of the event
     * @param to The end time of the event
     * @return Always returns false to continue execution
     * @throws MoistBotException if the task cannot be added or saved
     */
    private boolean executeEvent(String description, String from, String to) throws MoistBotException {
        return executeAddTask(taskManager.addEvent(description, from, to));
    }

    /**
     * Displays feedback after a task is added.
     *
     * @param task The added task
     * @return Always returns false to continue execution
     * @throws MoistBotException if the updated task list cannot be saved
     */
    private boolean executeAddTask(Task task) throws MoistBotException {
        try {
            Storage.saveTasks(taskManager);
        } catch (MoistBotException e) {
            taskManager.removeLastTask();
            throw e;
        }
        UserInterface.printAddTask(task, taskManager.getSize());
        return false;
    }

    /**
     * Executes the mark command to mark a task as completed.
     *
     * @param description The 1-based index of the task to mark
     * @return Always returns false to continue execution
     * @throws MoistBotException if the task index is invalid
     */
    private boolean executeMark(String description) throws MoistBotException {
        int markIndex = Integer.parseInt(description);
        Task task = taskManager.getExistingTask(markIndex, "mark");
        boolean wasCompleted = task.isCompleted();
        task.setCompleted(true);
        saveCompletionChange(task, wasCompleted);
        UserInterface.printMarkTask(task);
        return false;
    }

    /**
     * Executes the unmark command to mark a task as incomplete.
     *
     * @param description The 1-based index of the task to unmark
     * @return Always returns false to continue execution
     * @throws MoistBotException if the task index is invalid
     */
    private boolean executeUnmark(String description) throws MoistBotException {
        int unmarkIndex = Integer.parseInt(description);
        Task task = taskManager.getExistingTask(unmarkIndex, "unmark");
        boolean wasCompleted = task.isCompleted();
        task.setCompleted(false);
        saveCompletionChange(task, wasCompleted);
        UserInterface.printUnmarkTask(task);
        return false;
    }

    /**
     * Executes the delete command and removes the selected task.
     *
     * @param description The 1-based index of the task to delete
     * @return Always returns false to continue execution
     * @throws MoistBotException if the task index is invalid
     */
    private boolean executeDelete(String description) throws MoistBotException {
        int deleteIndex = Integer.parseInt(description);
        Task deletedTask = taskManager.getExistingTask(deleteIndex, "delete");
        taskManager.deleteTask(deleteIndex);
        try {
            Storage.saveTasks(taskManager);
        } catch (MoistBotException e) {
            taskManager.restoreTask(deleteIndex, deletedTask);
            throw e;
        }
        UserInterface.printDeleteTask(deletedTask, taskManager.getSize());
        return false;
    }

    /**
     * Saves a completion-state change and restores the previous state if persistence fails.
     */
    private void saveCompletionChange(Task task, boolean wasCompleted) throws MoistBotException {
        try {
            Storage.saveTasks(taskManager);
        } catch (MoistBotException e) {
            task.setCompleted(wasCompleted);
            throw e;
        }
    }

}

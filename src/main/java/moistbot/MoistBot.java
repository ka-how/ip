package moistbot;

import moistbot.command.Command;
import moistbot.command.Parser;
import moistbot.exception.MoistBotException;
import moistbot.storage.Storage;
import moistbot.task.TaskManager;
import moistbot.ui.UserInterface;

/**
 * MoistBot is a simple console-based task tracker application that teaches Java fundamentals.
 * The application accepts commands such as adding, listing, marking, unmarking, and deleting tasks.
 * Parsing and command handling are separated into helper classes (Parser, Command, TaskManager)
 * for clarity, maintainability, and easier testing.
 */
public final class MoistBot {
    /** The persistence service configured for this application instance. */
    private final Storage storage;

    /** The in-memory tasks owned by this application instance. */
    private final TaskManager taskManager;

    /** The console interface owned by this application instance. */
    private final UserInterface ui;

    /**
     * Creates a MoistBot application backed by the specified task data file.
     *
     * @param filePath The path used to load and save tasks
     */
    public MoistBot(String filePath) {
        storage = new Storage(filePath);
        taskManager = new TaskManager();
        ui = new UserInterface();
    }

    /**
     * Entry point of the MoistBot application.
     *
     * @param args Command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        new MoistBot("data/moistbot.txt").run();
    }

    /**
     * Runs the interactive command loop until the user exits or the input stream closes.
     * The scanner is scoped to the application lifetime, so System.in is closed when the
     * application terminates.
     */
    public void run() {
        ui.printWelcome();
        loadSavedTasks();

        boolean isExit = false;
        try (UserInterface activeUi = ui) {
            while (!isExit && activeUi.hasNextCommand()) {
                isExit = processCommand(activeUi.readCommand());
            }
        }
    }

    /**
     * Loads saved tasks while allowing the application to remain usable if loading fails.
     */
    private void loadSavedTasks() {
        try {
            taskManager.setTasks(storage.loadTasks());
        } catch (MoistBotException e) {
            ui.printMessage(e.getMessage());
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
            command.execute(taskManager, ui, storage);
            return command.isExit();
        } catch (MoistBotException e) {
            ui.printMessage(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            ui.printMessage("My apologies, but I could not process that command because of an unexpected "
                    + "internal error. Please check the command format and try again. If the matter persists, "
                    + "please restart MoistBot.");
            return false;
        }
    }

}

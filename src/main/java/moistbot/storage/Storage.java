package moistbot.storage;

import moistbot.exception.MoistBotException;
import moistbot.task.Deadline;
import moistbot.task.Event;
import moistbot.task.Task;
import moistbot.task.TaskManager;
import moistbot.task.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves MoistBot tasks using a text file on the local disk.
 */
public final class Storage {
    private static final Path DATA_FILE = Path.of("data", "moistbot.txt");

    /**
     * Prevents instantiation because storage operations do not require object state.
     */
    private Storage() {
    }

    /**
     * Restores tasks from the data file when it exists.
     * A missing file represents a new user and therefore starts with an empty list.
     *
     * @throws MoistBotException if the file cannot be read or contains invalid task data
     */
    public static void loadTasks() throws MoistBotException {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        List<String> taskLines;
        try {
            taskLines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MoistBotException("My apologies, but I could not read your saved task list. Please check "
                    + "that data/moistbot.txt is readable, then restart MoistBot.");
        }

        List<Task> tasks = new ArrayList<>();
        for (int lineNumber = 1; lineNumber <= taskLines.size(); lineNumber++) {
            String taskLine = taskLines.get(lineNumber - 1);
            if (!taskLine.isBlank()) {
                tasks.add(parseTask(taskLine, lineNumber));
            }
        }

        for (Task task : tasks) {
            addLoadedTask(task);
        }
    }

    /**
     * Writes the complete current task list, replacing the previous saved copy.
     *
     * @throws MoistBotException if the data directory or file cannot be written
     */
    public static void saveTasks() throws MoistBotException {
        List<String> taskLines = new ArrayList<>();
        for (int taskNumber = 1; taskNumber <= TaskManager.getSize(); taskNumber++) {
            taskLines.add(formatTask(TaskManager.getTask(taskNumber)));
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, taskLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new MoistBotException("My apologies, but I could not save your task list. Please check that "
                    + "the data folder is writable, then try your command again.");
        }
    }

    /**
     * Converts a task to a stable, pipe-separated representation for future loading.
     */
    private static String formatTask(Task task) {
        String completionStatus = task.isCompleted() ? "1" : "0";
        String commonFields = task.getTaskType() + " | " + completionStatus + " | " + task.getDescription();

        switch (task.getTaskType()) {
            case Task.TYPE_DEADLINE:
                return commonFields + " | " + ((Deadline) task).getDeadline();
            case Task.TYPE_EVENT:
                Event event = (Event) task;
                return commonFields + " | " + event.getFrom() + " | " + event.getTo();
            default:
                return commonFields;
        }
    }

    /**
     * Reconstructs one task while rejecting data that cannot be interpreted safely.
     */
    private static Task parseTask(String taskLine, int lineNumber) throws MoistBotException {
        String[] fields = taskLine.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            throw invalidDataException(lineNumber);
        }

        boolean isCompleted;
        if ("1".equals(fields[1])) {
            isCompleted = true;
        } else if ("0".equals(fields[1])) {
            isCompleted = false;
        } else {
            throw invalidDataException(lineNumber);
        }

        String description = fields[2];
        Task task;
        if (fields.length == 3 && "T".equals(fields[0])) {
            task = new Todo(description);
        } else if (fields.length == 4 && "D".equals(fields[0])) {
            task = new Deadline(description, fields[3]);
        } else if (fields.length == 5 && "E".equals(fields[0])) {
            task = new Event(description, fields[3], fields[4]);
        } else {
            throw invalidDataException(lineNumber);
        }

        if (description.isBlank() || hasBlankTaskDetail(fields)) {
            throw invalidDataException(lineNumber);
        }
        task.setCompleted(isCompleted);
        return task;
    }

    /**
     * Returns whether a deadline or event is missing one of its required time fields.
     */
    private static boolean hasBlankTaskDetail(String[] fields) {
        for (int fieldIndex = 3; fieldIndex < fields.length; fieldIndex++) {
            if (fields[fieldIndex].isBlank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a restored task through TaskManager so storage remains independent of its collection implementation.
     */
    private static void addLoadedTask(Task task) throws MoistBotException {
        Task addedTask;
        switch (task.getTaskType()) {
            case Task.TYPE_DEADLINE:
                addedTask = TaskManager.addDeadline(task.getDescription(), ((Deadline) task).getDeadline());
                break;
            case Task.TYPE_EVENT:
                Event event = (Event) task;
                addedTask = TaskManager.addEvent(task.getDescription(), event.getFrom(), event.getTo());
                break;
            default:
                addedTask = TaskManager.addTodo(task.getDescription());
                break;
        }
        addedTask.setCompleted(task.isCompleted());
    }

    /**
     * Creates a consistent, actionable error for malformed saved data.
     */
    private static MoistBotException invalidDataException(int lineNumber) {
        return new MoistBotException("My apologies, but I could not load your saved tasks because line "
                + lineNumber + " in data/moistbot.txt is invalid. Please correct or remove the file, then "
                + "restart MoistBot.");
    }
}

package moistbot.storage;

import moistbot.exception.MoistBotException;
import moistbot.task.Deadline;
import moistbot.task.Event;
import moistbot.task.Task;
import moistbot.task.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves MoistBot tasks in a text file so that a later version can restore them.
 */
public final class Storage {
    private static final Path DATA_FILE = Path.of("data", "moistbot.txt");

    /**
     * Prevents instantiation because storage operations do not require object state.
     */
    private Storage() {
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
}

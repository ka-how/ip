package moistbot.task;

import java.util.ArrayList;

/**
 * Manages a shared, dynamically sized list of tasks stored in memory.
 * Provides functionality to add, retrieve, and delete tasks.
 */
public final class TaskManager {
    private static final ArrayList<Task> TASK_LIST = new ArrayList<>();

    /**
     * Prevents instantiation because the application uses one shared in-memory task list.
     */
    private TaskManager() {
    }

    /**
     * Adds a new todo task to the task list.
     *
     * @param description The description of the todo task
     * @return The task that was added
     */
    public static Task addTodo(String description) {
        return addTask(new Todo(description));
    }

    /**
     * Adds a new deadline task to the task list.
     *
     * @param description The description of the deadline task
     * @param by The deadline for the task
     * @return The task that was added
     */
    public static Task addDeadline(String description, String by) {
        return addTask(new Deadline(description, by));
    }

    /**
     * Adds a new event task to the task list.
     *
     * @param description The description of the event
     * @param from The start time of the event
     * @param to The end time of the event
     * @return The task that was added
     */
    public static Task addEvent(String description, String from, String to) {
        return addTask(new Event(description, from, to));
    }

    /**
     * Stores a task in the dynamically sized task list.
     *
     * @param task The task to store
     * @return The task that was stored
     */
    private static Task addTask(Task task) {
        TASK_LIST.add(task);
        return task;
    }

    /**
     * Deletes and returns a task using its 1-based list number.
     *
     * @param id The 1-based task number
     * @return The task removed from the list
     */
    public static Task deleteTask(int id) {
        return TASK_LIST.remove(id - 1);
    }

    /**
     * Retrieves a task from the list by its 1-based index.
     * Returns null if the index is outside the valid range.
     *
     * @param id The 1-based task index (1 refers to the first task)
     * @return The task at the given index, or null if the index is out of bounds
     */
    public static Task getTask(int id) {
        if (id < 1 || id > TASK_LIST.size()) {
            return null;
        }
        return TASK_LIST.get(id - 1);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return The count of tasks stored in the list
     */
    public static int getSize() {
        return TASK_LIST.size();
    }
}

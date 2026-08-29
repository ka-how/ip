/**
 * Manages a static list of tasks stored in memory.
 * Provides functionality to add tasks and display all stored tasks.
 * The list can store up to 100 tasks using a fixed-size array.
 */
public class TaskManager {
    private static final Task[] TASK_ARRAY = new Task[100];
    private static int size = 0;

    /**
     * Adds a new task to the task list.
     * Increments the task count if there is space available.
     *
     * @param task The task to add to the list
     * @throws IllegalArgumentException if the task list is at maximum capacity (100 tasks)
     */
    public static void addTask(String task) {
        if (size == TASK_ARRAY.length) {
            throw new IllegalArgumentException("Memory full!");
        }
        TASK_ARRAY[size] = new Task(task);
        size++;
    }

    /**
     * Retrieves a task from the list by its 1-based index.
     *
     * @param id The 1-based task index (1 is the first task)
     * @return The task at the given index, or null if the index is out of bounds
     */
    public static Task getTask(int id) {
        if (id < 1 || id > size) {
            return null;
        }
        return TASK_ARRAY[id - 1];
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return The count of tasks in the list
     */
    public static int getSize() {
        return size;
    }

    /**
     * Returns the underlying task array.
     *
     * @return The task array (may contain null elements after the last task)
     */
    public static Task[] getTaskArray() {
        return TASK_ARRAY;
    }
}

/**
 * Manages a static list of tasks stored in memory.
 * Provides functionality to add tasks of various types (todo, deadline, event)
 * and retrieve tasks by index.
 * The list can store up to 100 tasks using a fixed-size array.
 */
public class TaskManager {
    private static final Task[] TASK_ARRAY = new Task[100];
    private static int size = 0;

    /**
     * Adds a new todo task to the task list.
     * Wrapper method that delegates to addTodo(String).
     *
     * @param description The description of the todo task to add
     * @throws IllegalArgumentException if the task list is at maximum capacity (100 tasks)
     */
    public static void addTask(String description) {
        addTodo(description);
    }

    /**
     * Adds a new todo task to the task list.
     * Increments the size counter if there is space available.
     *
     * @param description The description of the todo task
     * @throws IllegalArgumentException if the task list is at maximum capacity (100 tasks)
     */
    public static void addTodo(String description) {
        if (size == TASK_ARRAY.length) {
            throw new IllegalArgumentException("Memory full!");
        }
        TASK_ARRAY[size] = new Todo(description);
        size++;
    }

    /**
     * Adds a new deadline task to the task list.
     * Increments the size counter if there is space available.
     *
     * @param description The description of the deadline task
     * @param by The deadline for the task
     * @throws IllegalArgumentException if the task list is at maximum capacity (100 tasks)
     */
    public static void addDeadline(String description, String by) {
        if (size == TASK_ARRAY.length) {
            throw new IllegalArgumentException("Memory full!");
        }
        TASK_ARRAY[size] = new Deadline(description, by);
        size++;
    }

    /**
     * Adds a new event task to the task list.
     * Increments the size counter if there is space available.
     *
     * @param description The description of the event
     * @param from The start time of the event
     * @param to The end time of the event
     * @throws IllegalArgumentException if the task list is at maximum capacity (100 tasks)
     */
    public static void addEvent(String description, String from, String to) {
        if (size == TASK_ARRAY.length) {
            throw new IllegalArgumentException("Memory full!");
        }
        TASK_ARRAY[size] = new Event(description, from, to);
        size++;
    }

    /**
     * Retrieves a task from the list by its 1-based index.
     * Returns null if the index is outside the valid range.
     *
     * @param id The 1-based task index (1 refers to the first task)
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
     * @return The count of tasks stored in the list
     */
    public static int getSize() {
        return size;
    }

    /**
     * Returns the underlying task array.
     * Note: The array may contain null elements after the last valid task.
     *
     * @return The task array with capacity for up to 100 tasks
     */
    public static Task[] getTaskArray() {
        return TASK_ARRAY;
    }
}

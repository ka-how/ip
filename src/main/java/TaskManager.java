/**
 * Manages a static list of tasks stored in memory.
 * Provides functionality to add tasks of various types (todo, deadline, event)
 * and retrieve tasks by index.
 * The list can store up to 100 tasks using a fixed-size array.
 */
public final class TaskManager {
    private static final int MAX_TASKS = 100;
    private static final Task[] TASK_ARRAY = new Task[MAX_TASKS];
    private static int size = 0;

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
     * @throws MoistBotException if the task list is at maximum capacity (100 tasks)
     */
    public static Task addTodo(String description) throws MoistBotException {
        return addTask(new Todo(description));
    }

    /**
     * Adds a new deadline task to the task list.
     *
     * @param description The description of the deadline task
     * @param by The deadline for the task
     * @return The task that was added
     * @throws MoistBotException if the task list is at maximum capacity (100 tasks)
     */
    public static Task addDeadline(String description, String by) throws MoistBotException {
        return addTask(new Deadline(description, by));
    }

    /**
     * Adds a new event task to the task list.
     *
     * @param description The description of the event
     * @param from The start time of the event
     * @param to The end time of the event
     * @return The task that was added
     * @throws MoistBotException if the task list is at maximum capacity (100 tasks)
     */
    public static Task addEvent(String description, String from, String to) throws MoistBotException {
        return addTask(new Event(description, from, to));
    }

    /**
     * Stores a task after ensuring that the fixed-size task list has capacity.
     *
     * @param task The task to store
     * @return The task that was stored
     * @throws MoistBotException if the task list is at maximum capacity
     */
    private static Task addTask(Task task) throws MoistBotException {
        if (size == MAX_TASKS) {
            throw new MoistBotException("My apologies, but your task list is full (maximum 100 tasks). No task "
                    + "has been added.");
        }
        TASK_ARRAY[size] = task;
        size++;
        return task;
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
}

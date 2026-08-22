/**
 * Represents a single task in the task list.
 * Each task has a name and a completion status that can be toggled.
 */
public class Task {
    private String name;
    private boolean isCompleted;

    /**
     * Constructs a Task with the given name.
     * Tasks are initialized as not completed.
     *
     * @param name The name or description of the task
     */
    public Task(String name) {
        this.name = name;
        isCompleted = false;
    }

    /**
     * Sets the completion status of this task.
     *
     * @param isCompleted True if the task is completed, false otherwise
     */
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * Prints this task's details in the format "[X] taskname" (if completed)
     * or "[ ] taskname" (if not completed). Does not print a newline.
     */
    public void printDetails() {
        char cross = isCompleted ? 'X' : ' ';
        System.out.print("[" + cross + "] " + name);
    }
}

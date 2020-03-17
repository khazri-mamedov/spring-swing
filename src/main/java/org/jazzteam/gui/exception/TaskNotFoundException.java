package org.jazzteam.gui.exception;

public class TaskNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3235471108603084116L;

    public TaskNotFoundException(int taskId) {
        super(String.format("Task with index %d not found", taskId));
    }
}

package org.jazzteam.gui.exception;

public class DtoNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3235471108603084116L;

    public DtoNotFoundException(int taskId) {
        super(String.format("Task with index %d not found", taskId));
    }
}

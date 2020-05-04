package org.example.gui.event.task;

import lombok.Getter;
import org.example.dto.TaskDto;
import org.springframework.context.ApplicationEvent;

public class EditEvent extends ApplicationEvent {
    private static final long serialVersionUID = -6027831971235701327L;

    @Getter
    private final TaskDto editedTaskDto;

    public EditEvent(Object source, TaskDto editedTaskDto) {
        super(source);
        this.editedTaskDto = editedTaskDto;
    }
}

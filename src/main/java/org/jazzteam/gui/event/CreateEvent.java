package org.jazzteam.gui.event;

import lombok.Getter;
import org.jazzteam.dto.TaskDto;
import org.springframework.context.ApplicationEvent;

public class CreateEvent extends ApplicationEvent {
    private static final long serialVersionUID = 5369402902472618869L;

    @Getter
    private TaskDto savedTaskDto;

    public CreateEvent(Object source, TaskDto savedTaskDto) {
        super(source);
        this.savedTaskDto = savedTaskDto;
    }
}

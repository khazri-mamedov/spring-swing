package org.example.gui.event.task;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SwapEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7708964335013207608L;

    @Getter
    private final int firstSelectedTaskId;
    @Getter
    private final int secondSelectedTaskId;

    public SwapEvent(Object source, int firstSelectedTaskId, int secondSelectedTaskId) {
        super(source);
        this.firstSelectedTaskId = firstSelectedTaskId;
        this.secondSelectedTaskId = secondSelectedTaskId;
    }
}

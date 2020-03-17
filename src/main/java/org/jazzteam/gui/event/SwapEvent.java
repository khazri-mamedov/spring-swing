package org.jazzteam.gui.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SwapEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7708964335013207608L;

    @Getter
    private final int firsSelectedTaskId;
    @Getter
    private final int secondSelectedTaskId;

    public SwapEvent(Object source, int firsSelectedTaskId, int secondSelectedTaskId) {
        super(source);
        this.firsSelectedTaskId = firsSelectedTaskId;
        this.secondSelectedTaskId = secondSelectedTaskId;
    }
}

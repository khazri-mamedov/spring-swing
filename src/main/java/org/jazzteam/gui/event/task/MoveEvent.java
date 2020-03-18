package org.jazzteam.gui.event.task;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MoveEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6903983662787400834L;

    @Getter
    private final int firstSelectedTaskId;
    @Getter
    private final int secondSelectedTaskId;
    @Getter
    private final MoveEventType moveEventType;

    public MoveEvent(Object source, int firstSelectedTaskId, int secondSelectedTaskId, MoveEventType moveEventType) {
        super(source);
        this.firstSelectedTaskId = firstSelectedTaskId;
        this.secondSelectedTaskId = secondSelectedTaskId;
        this.moveEventType = moveEventType;
    }
}

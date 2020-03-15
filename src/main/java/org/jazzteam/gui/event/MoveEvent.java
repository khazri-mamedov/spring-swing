package org.jazzteam.gui.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MoveEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6903983662787400834L;

    @Getter
    private int selectedRow;
    @Getter
    private MoveEventType moveEventType;

    public MoveEvent(Object source, int selectedRow, MoveEventType moveEventType) {
        super(source);
        this.selectedRow = selectedRow;
        this.moveEventType = moveEventType;
    }
}

package org.jazzteam.gui.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MoveEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6903983662787400834L;

    @Getter
    private int selectedRow;

    public MoveEvent(Object source, int selectedRow) {
        super(source);
        this.selectedRow = selectedRow;
    }
}

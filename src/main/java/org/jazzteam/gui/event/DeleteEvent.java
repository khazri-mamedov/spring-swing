package org.jazzteam.gui.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class DeleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = -7885218992452621746L;

    @Getter
    private int deletedTaskId;

    public DeleteEvent(Object source, int deletedTaskId) {
        super(source);
        this.deletedTaskId = deletedTaskId;
    }
}

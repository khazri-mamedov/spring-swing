package org.jazzteam.gui.event.performer;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class DeleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = 4541041913847744289L;

    @Getter
    private int deletedPerformerId;

    public DeleteEvent(Object source, int deletedPerformerId) {
        super(source);
        this.deletedPerformerId = deletedPerformerId;
    }
}

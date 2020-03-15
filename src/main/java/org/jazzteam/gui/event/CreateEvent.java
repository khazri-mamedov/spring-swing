package org.jazzteam.gui.event;

import org.springframework.context.ApplicationEvent;

public class CreateEvent extends ApplicationEvent {
    private static final long serialVersionUID = 5369402902472618869L;

    public CreateEvent(Object source) {
        super(source);
    }
}

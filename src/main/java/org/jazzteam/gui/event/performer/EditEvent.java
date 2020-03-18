package org.jazzteam.gui.event.performer;

import lombok.Getter;
import org.jazzteam.dto.PerformerDto;
import org.springframework.context.ApplicationEvent;

public class EditEvent extends ApplicationEvent {
    private static final long serialVersionUID = -5756918127635105757L;

    @Getter
    private final PerformerDto editedPerformerDto;

    public EditEvent(Object source, PerformerDto performerDto) {
        super(source);
        this.editedPerformerDto = performerDto;
    }
}

package org.jazzteam.gui.event.performer;

import lombok.Getter;
import org.jazzteam.dto.PerformerDto;
import org.springframework.context.ApplicationEvent;

public class CreateEvent extends ApplicationEvent {
    private static final long serialVersionUID = -1018036549550022717L;

    @Getter
    private PerformerDto savedPerformerDto;

    public CreateEvent(Object source, PerformerDto savedPerformerDto) {
        super(source);
        this.savedPerformerDto = savedPerformerDto;
    }
}

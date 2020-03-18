package org.jazzteam.gui.action.performer;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.gui.event.performer.EditEvent;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class EditAction implements PerformerAction {
    private static final long serialVersionUID = 7771106022831189590L;

    private final PerformerDto updatedPerformerDto;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new EditEvent(this, updatedPerformerDto));
    }
}

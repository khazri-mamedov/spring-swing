package org.jazzteam.gui.action.performer;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.gui.event.performer.CreateEvent;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class CreateAction implements PerformerAction {
    private static final long serialVersionUID = -6100060982131186922L;

    private final PerformerDto performerDto;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new CreateEvent(this, performerDto));
    }
}

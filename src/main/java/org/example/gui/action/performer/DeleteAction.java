package org.example.gui.action.performer;

import lombok.RequiredArgsConstructor;
import org.example.gui.event.performer.DeleteEvent;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class DeleteAction implements PerformerAction {
    private static final long serialVersionUID = -1440222516376346223L;

    private final int deletedId;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new DeleteEvent(this, deletedId));
    }
}

package org.example.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.example.gui.event.task.MoveEvent;
import org.example.gui.event.task.MoveEventType;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class MoveAction implements TaskAction {
    private static final long serialVersionUID = -3360460916617543808L;

    private final int firstSelectedTaskId;
    private final int secondSelectedTaskId;
    private final MoveEventType moveEventType;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(
                new MoveEvent(this, firstSelectedTaskId, secondSelectedTaskId, moveEventType));
    }
}

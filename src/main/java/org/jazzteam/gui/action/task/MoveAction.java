package org.jazzteam.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.event.MoveEvent;
import org.jazzteam.gui.event.MoveEventType;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class MoveAction implements TaskAction {
    private static final long serialVersionUID = -3360460916617543808L;

    private final int firstSelectedTaskId;
    private final int secondSelectedTaskId;
    private final MoveEventType moveEventType;

    @Override
    public void execute(
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(
                new MoveEvent(this, firstSelectedTaskId, secondSelectedTaskId, moveEventType));
    }
}

package org.jazzteam.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.event.SwapEvent;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class SwapAction implements TaskAction {
    private static final long serialVersionUID = 7444961021937684170L;

    private final int firstSelectedTaskId;
    private final int secondSelectedTaskId;

    @Override
    public void execute(
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new SwapEvent(this, firstSelectedTaskId, secondSelectedTaskId));
    }
}

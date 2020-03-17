package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.event.DeleteEvent;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class DeleteAction implements TaskAction {
    private static final long serialVersionUID = 6386618224357460702L;

    private final int deletedTaskId;

    @Override
    public void execute(
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new DeleteEvent(this, deletedTaskId));
    }
}

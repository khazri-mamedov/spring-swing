package org.example.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.example.gui.event.task.DeleteEvent;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class DeleteAction implements TaskAction {
    private static final long serialVersionUID = 6386618224357460702L;

    private final int deletedTaskId;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new DeleteEvent(this, deletedTaskId));
    }
}

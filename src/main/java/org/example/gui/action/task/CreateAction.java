package org.example.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskDto;
import org.example.gui.event.task.CreateEvent;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class CreateAction implements TaskAction {
    private static final long serialVersionUID = -2634999256402806653L;

    private final TaskDto createdTaskDto;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new CreateEvent(this, createdTaskDto));
    }
}

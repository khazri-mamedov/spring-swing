package org.jazzteam.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.EditEvent;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class EditAction implements TaskAction {
    private static final long serialVersionUID = 8022537311553858959L;

    private final TaskDto updatedTaskDto;

    @Override
    public void execute(
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new EditEvent(this, updatedTaskDto));
    }
}

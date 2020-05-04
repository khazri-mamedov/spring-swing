package org.example.gui.action.task;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskDto;
import org.example.gui.event.task.EditEvent;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class EditAction implements TaskAction {
    private static final long serialVersionUID = 8022537311553858959L;

    private final TaskDto updatedTaskDto;

    @Override
    public void execute(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new EditEvent(this, updatedTaskDto));
    }
}

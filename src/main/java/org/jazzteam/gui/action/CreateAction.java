package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.CreateEvent;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class CreateAction implements TaskAction {
    private static final long serialVersionUID = -2634999256402806653L;

    private final TaskDto createdTaskDto;

    @Override
    public void execute(
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new CreateEvent(this, createdTaskDto));
    }
}

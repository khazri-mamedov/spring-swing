package org.jazzteam.gui.action;

import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.repository.TaskRepository;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface TaskAction extends Serializable {
    void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher);
}

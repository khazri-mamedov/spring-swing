package org.jazzteam.gui.action;

import org.jazzteam.gui.table.TaskTable;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface TaskAction extends Serializable {
    void execute(
            TaskTable taskTable,
            TaskRepository taskRepository,
            TaskMapper taskMapper,
            ApplicationEventPublisher applicationEventPublisher);
}

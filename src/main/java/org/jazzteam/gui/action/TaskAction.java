package org.jazzteam.gui.action;

import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface TaskAction extends Serializable {
    void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher);
}

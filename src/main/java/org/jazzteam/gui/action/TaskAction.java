package org.jazzteam.gui.action;

import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface TaskAction extends Serializable {
    void execute(TaskService taskService, ApplicationEventPublisher applicationEventPublisher);
}

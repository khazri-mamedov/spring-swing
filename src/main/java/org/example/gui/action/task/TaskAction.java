package org.example.gui.action.task;

import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface TaskAction extends Serializable {
    void execute(ApplicationEventPublisher applicationEventPublisher);
}

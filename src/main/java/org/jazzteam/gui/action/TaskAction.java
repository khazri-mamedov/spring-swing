package org.jazzteam.gui.action;

import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface TaskAction extends Serializable {
    void execute(ApplicationEventPublisher applicationEventPublisher);
}

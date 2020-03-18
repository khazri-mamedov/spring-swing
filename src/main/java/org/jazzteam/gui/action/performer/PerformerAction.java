package org.jazzteam.gui.action.performer;

import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public interface PerformerAction extends Serializable {
    void execute(ApplicationEventPublisher applicationEventPublisher);
}

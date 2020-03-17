package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.SwapEvent;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;
import java.util.Collections;

@RequiredArgsConstructor
public class SwapAction implements TaskAction {
    private static final long serialVersionUID = 7444961021937684170L;

    private final int firstSelectedTaskId;
    private final int secondSelectedTaskId;

    @Override
    public void execute(
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new SwapEvent(this, firstSelectedTaskId, secondSelectedTaskId));
    }
}

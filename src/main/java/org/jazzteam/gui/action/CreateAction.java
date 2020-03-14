package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class CreateAction implements TaskAction {
    private static final long serialVersionUID = -2634999256402806653L;

    private final int savedTaskEntityId;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        taskTableModel.addRow(taskService.getById(savedTaskEntityId));
    }
}

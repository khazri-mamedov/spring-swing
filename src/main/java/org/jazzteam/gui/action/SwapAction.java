package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;
import java.util.Collections;

@RequiredArgsConstructor
public class SwapAction implements TaskAction {
    private static final long serialVersionUID = 7444961021937684170L;

    private final TaskDto firstSelectedTask;
    private final TaskDto secondSelectedTask;
    private final int firstSelectedRow;
    private final int secondSelectedRow;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        Collections.swap(taskTableModel.getTasks(), firstSelectedRow, secondSelectedRow);
        EventQueue.invokeLater(() -> {
            taskTableModel.setValueAt(firstSelectedTask, secondSelectedRow);
            taskTableModel.setValueAt(secondSelectedTask, firstSelectedRow);
        });
    }
}

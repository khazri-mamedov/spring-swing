package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.event.MoveEvent;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;

@RequiredArgsConstructor
public class MoveAction implements TaskAction {
    private static final long serialVersionUID = -3360460916617543808L;

    private final int selectedRow;
    private final int selectedOrderId;
    private final int prevOrderId;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        int prevRow = selectedRow - 1;
        applicationEventPublisher.publishEvent(new MoveEvent(this, prevRow));
        EventQueue.invokeLater(() -> {
            taskTableModel.moveRow(selectedRow, selectedRow, prevRow);
            taskTableModel.setValueAt(selectedOrderId, prevRow, TaskTableModel.Column.ORDER.ordinal());
            taskTableModel.setValueAt(prevOrderId, selectedRow, TaskTableModel.Column.ORDER.ordinal());
        });
    }
}

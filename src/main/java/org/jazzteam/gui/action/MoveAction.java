package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.event.MoveEvent;
import org.jazzteam.gui.event.MoveEventType;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;
import java.util.Collections;

@RequiredArgsConstructor
public class MoveAction implements TaskAction {
    private static final long serialVersionUID = -3360460916617543808L;

    private final int selectedRow;
    private final int rowIndex;
    private final int selectedOrderId;
    private final int prevOrderId;
    private final MoveEventType moveEventType;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        Collections.swap(taskTableModel.getTasks(), rowIndex, selectedRow);
        applicationEventPublisher.publishEvent(new MoveEvent(this, selectedRow, moveEventType));
        EventQueue.invokeLater(() -> {
            taskTableModel.moveRow(selectedRow, selectedRow, rowIndex);
            taskTableModel.setValueAt(selectedOrderId, rowIndex, TaskTableModel.Column.ORDER.ordinal());
            taskTableModel.setValueAt(prevOrderId, selectedRow, TaskTableModel.Column.ORDER.ordinal());
        });
    }
}

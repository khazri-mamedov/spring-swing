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

    private final int firstSelectedRow;
    private final int secondSelectedRow;
    private final int firstSelectedOrderId;
    private final int secondSelectedOrderId;
    private final MoveEventType moveEventType;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        Collections.swap(taskTableModel.getTasks(), secondSelectedRow, firstSelectedRow);
        EventQueue.invokeLater(() -> {
            taskTableModel.moveRow(firstSelectedRow, firstSelectedRow, secondSelectedRow);
            taskTableModel.setValueAt(firstSelectedOrderId, secondSelectedRow, TaskTableModel.Column.ORDER.ordinal());
            taskTableModel.setValueAt(secondSelectedOrderId, firstSelectedRow, TaskTableModel.Column.ORDER.ordinal());
        });
        applicationEventPublisher.publishEvent(new MoveEvent(this, firstSelectedRow, moveEventType));
    }
}

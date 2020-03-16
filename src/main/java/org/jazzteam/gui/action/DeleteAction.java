package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.event.DeleteEvent;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;

@RequiredArgsConstructor
public class DeleteAction implements TaskAction {
    private static final long serialVersionUID = 6386618224357460702L;

    private final int selectedRow;
    private final int deletedTaskId;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        // Avoids unnecessary down casting
        DeleteEvent deleteEvent = new DeleteEvent(this, deletedTaskId);
        applicationEventPublisher.publishEvent(deleteEvent);
        EventQueue.invokeLater(() -> taskTableModel.removeRow(selectedRow));
    }
}

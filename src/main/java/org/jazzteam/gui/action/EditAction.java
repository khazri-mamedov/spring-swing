package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.EditEvent;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;

@RequiredArgsConstructor
public class EditAction implements TaskAction {
    private static final long serialVersionUID = 8022537311553858959L;

    private final TaskDto updatedTaskDto;
    private final int selectedRow;

    @Override
    public void execute(
            TaskTableModel taskTableModel,
            TaskService taskService,
            ApplicationEventPublisher applicationEventPublisher) {
        taskTableModel.getTasks().set(selectedRow, updatedTaskDto);
        EditEvent editEvent = new EditEvent(this, updatedTaskDto);
        EventQueue.invokeLater(() -> {
            taskTableModel.setValueAt(updatedTaskDto, selectedRow);
        });
        applicationEventPublisher.publishEvent(editEvent);
    }
}

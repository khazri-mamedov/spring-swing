package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.EditEvent;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.*;

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
        ExecutorDto executorDto = updatedTaskDto.getExecutor();
        EditEvent editEvent = new EditEvent(this, updatedTaskDto);
        applicationEventPublisher.publishEvent(editEvent);
        EventQueue.invokeLater(() -> {
            taskTableModel.setValueAt(updatedTaskDto.getName(), selectedRow, 0);
            taskTableModel.setValueAt(updatedTaskDto.getDescription(), selectedRow, 1);
            taskTableModel.setValueAt(
                    String.format("%s %s", executorDto.getFirstName(), executorDto.getLastName()),
                    selectedRow,
                    2
            );
        });

    }
}

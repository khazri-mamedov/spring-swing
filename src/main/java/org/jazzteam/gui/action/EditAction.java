package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.table.TaskTable;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.awt.EventQueue;

@RequiredArgsConstructor
public class EditAction implements TaskAction {
    private static final long serialVersionUID = 8022537311553858959L;

    private final TaskDto updatedTaskDto;
    private final int selectedRow;

    @Override
    public void execute(
            TaskTable taskTable,
            TaskRepository taskRepository,
            TaskMapper taskMapper,
            ApplicationEventPublisher applicationEventPublisher) {
        TaskTableModel taskTableModel = taskTable.getTableModel();
        taskTableModel.getTasks().set(selectedRow, updatedTaskDto);
        ExecutorDto executorDto = updatedTaskDto.getExecutor();
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

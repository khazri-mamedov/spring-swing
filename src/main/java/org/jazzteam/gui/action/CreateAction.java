package org.jazzteam.gui.action;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.table.TaskTable;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.model.TaskEntity;
import org.jazzteam.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
public class CreateAction implements TaskAction {
    private static final long serialVersionUID = -2634999256402806653L;

    private final int savedTaskEntityId;

    @Override
    public void execute(
            TaskTable taskTable,
            TaskRepository taskRepository,
            TaskMapper taskMapper,
            ApplicationEventPublisher applicationEventPublisher) {
        TaskEntity taskEntity
                = taskRepository.findById(savedTaskEntityId).orElseThrow(EntityNotFoundException::new);
        TaskDto savedTaskDto = taskMapper.toDto(taskEntity);
        taskTable.getTableModel().addRow(savedTaskDto);
    }
}

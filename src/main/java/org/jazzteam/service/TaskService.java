package org.jazzteam.service;

import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.MoveEventType;
import org.jazzteam.gui.table.TaskTableModel;

import java.util.List;
import java.util.Set;

public interface TaskService {
    List<TaskDto> getAllTasks();

    void deleteSelectedTask(TaskDto taskDto);

    void deleteById(int id);

    void updateTask(TaskDto taskDto, int rowIndex);

    void createTask(TaskDto taskDto);

    void moveTask(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto, MoveEventType moveEventType);

    void swapTasks(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto);
}

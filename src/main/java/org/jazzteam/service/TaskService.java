package org.jazzteam.service;

import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.MoveEventType;

import java.util.List;

public interface TaskService {
    List<TaskDto> getAllTasks();

    void deleteSelectedTask(TaskDto taskDto);

    void deleteById(int id);

    void updateTask(TaskDto taskDto);

    void createTask(TaskDto taskDto);

    void moveTask(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto, MoveEventType moveEventType);

    void swapTasks(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto);
}

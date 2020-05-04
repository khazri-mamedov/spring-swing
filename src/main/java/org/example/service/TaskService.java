package org.example.service;

import org.example.dto.TaskDto;
import org.example.gui.event.task.MoveEventType;

import java.util.List;

public interface TaskService {
    List<TaskDto> getAllTasksOrdered();

    void deleteSelected(TaskDto taskDto);

    void deleteById(Integer id);

    void update(TaskDto taskDto);

    void create(TaskDto taskDto);

    void moveTask(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto, MoveEventType moveEventType);

    void swapTasks(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto);
}

package org.jazzteam.service;

import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.MoveEventType;
import org.jazzteam.gui.table.TaskTableModel;

import java.util.List;
import java.util.Set;

public interface TaskService {
    TaskTableModel createAndPopulateTaskTableModel();

    List<TaskDto> getAllTasks();

    void deleteSelectedTask(int rowIndex);

    void deleteById(int id);

    TaskDto getSelectedTask(int rowIndex);

    void updateTask(TaskDto taskDto, int rowIndex);

    void createTask(TaskDto taskDto);

    void addTask(TaskDto savedTaskDto);

    void moveTask(int selectedRowIndex, int rowIndex, MoveEventType moveEventType);

    int getTaskCount();

    List<TaskDto> getSelectedTasks(Set<Integer> rowIndices);

    void swapTasks(int firstSelectedRow, int secondSelectedRow);
}

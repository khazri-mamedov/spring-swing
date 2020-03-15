package org.jazzteam.service;

import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.table.TaskTableModel;

import java.util.List;

public interface TaskService {
    TaskTableModel createAndPopulateTaskTableModel();
    List<TaskDto> getAllTasks();
    void deleteSelectedTask(int rowIndex);
    void deleteById(int id);
    TaskDto getSelectedTask(int rowIndex);
    void updateTask(TaskDto taskDto, int rowIndex);
    void createTask(TaskDto taskDto);
    TaskTableModel createAndRepopulateModel();
    void moveUpTask(int selectedRowIndex, TaskDto selectedTaskDto);

}

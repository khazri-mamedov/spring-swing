package org.jazzteam.gui.table.task;

import lombok.Getter;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.exception.TaskNotFoundException;

import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class TaskTableModel extends DefaultTableModel {
    private static final long serialVersionUID = -658016799037210612L;

    @Getter
    private List<TaskDto> tasks = new LinkedList<>();

    public TaskTableModel() {
        super(new String[]{"Name", "Description", "Performer", "Order", "Executed At"},
                0);
    }

    public enum Column {
        NAME, DESCRIPTION, PERFORMER, ORDER, EXECUTED_AT,
    }

    /**
     * Add row backed by container
     *
     * @param taskDto container's task
     */
    public void addRow(TaskDto taskDto) {
        tasks.add(taskDto);
        Object[] task = createRowObject(taskDto);
        super.addRow(task);
    }

    public void insertRow(TaskDto taskDto) {
        tasks.add(taskDto);
        tasks = tasks
                .parallelStream()
                .sorted(Comparator.comparingInt(TaskDto::getOrderId))
                .collect(Collectors.toList());
        int insertedIndex = tasks.indexOf(taskDto);
        Object[] task = createRowObject(taskDto);
        EventQueue.invokeLater(() -> insertRow(insertedIndex, task));
    }

    public void insertRows(TaskDto... taskDtos) {
        Arrays.stream(taskDtos).forEach(this::insertRow);
    }

    public void setValueAt(TaskDto taskDto, int rowIndex) {
        tasks.set(rowIndex, taskDto);

        PerformerDto performerDto = taskDto.getPerformer();
        String executorName = String.format("%s %s", performerDto.getFirstName(), performerDto.getLastName());

        setValueAt(taskDto.getName(), rowIndex, Column.NAME.ordinal());
        setValueAt(taskDto.getDescription(), rowIndex, Column.DESCRIPTION.ordinal());
        setValueAt(executorName, rowIndex, Column.PERFORMER.ordinal());
        setValueAt(taskDto.getOrderId(), rowIndex, Column.ORDER.ordinal());
        setValueAt(taskDto.getExecutedAt(), rowIndex, Column.EXECUTED_AT.ordinal());
    }

    @Override
    public void removeRow(int deletedTaskId) {
        int rowIndex = getTaskRowIndex(deletedTaskId);
        tasks.remove(rowIndex);
        super.removeRow(rowIndex);
    }

    public void removeRows(int... deletedTaskIds) {
        Arrays.stream(deletedTaskIds).forEach(this::removeRow);
    }

    public void moveTasks(int firsSelectedTaskId, int secondSelectedTaskId) {
        LinkedList<TaskDto> taskDtos = getTasksById(firsSelectedTaskId, secondSelectedTaskId);
        if (taskDtos.size() == 2) {
            int firstSelectedRow = tasks.indexOf(taskDtos.getFirst());
            int secondSelectedRow = tasks.indexOf(taskDtos.getLast());
            swapOrderIds(taskDtos.getFirst(), taskDtos.getLast());
            Collections.swap(tasks, firstSelectedRow, secondSelectedRow);
            EventQueue.invokeLater(() -> {
                moveRow(firstSelectedRow, firstSelectedRow, secondSelectedRow);
                setValueAt(taskDtos.getFirst(), secondSelectedRow);
                setValueAt(taskDtos.getLast(), firstSelectedRow);
            });
        }
    }

    public void swapTasks(int firsSelectedTaskId, int secondSelectedTaskId) {
        // Normal code duplication. Multiple swap possibility and different logic
        LinkedList<TaskDto> taskDtos = getTasksById(firsSelectedTaskId, secondSelectedTaskId);
        if (taskDtos.size() == 2) {
            int firstSelectedRow = tasks.indexOf(taskDtos.getFirst());
            int secondSelectedRow = tasks.indexOf(taskDtos.getLast());
            swapOrderIds(taskDtos.getFirst(), taskDtos.getLast());
            Collections.swap(tasks, firstSelectedRow, secondSelectedRow);
            EventQueue.invokeLater(() -> {
                setValueAt(taskDtos.getFirst(), secondSelectedRow);
                setValueAt(taskDtos.getLast(), firstSelectedRow);
            });
        }
    }

    private LinkedList<TaskDto> getTasksById(int firsSelectedTaskId, int secondSelectedTaskId) {
        return tasks
                .stream()
                .filter(taskDto ->
                        taskDto.getId().equals(firsSelectedTaskId) || taskDto.getId().equals(secondSelectedTaskId))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private void swapOrderIds(TaskDto prevTaskDto, TaskDto selectedTaskDto) {
        int swapOrderId = prevTaskDto.getOrderId();
        prevTaskDto.setOrderId(selectedTaskDto.getOrderId());
        selectedTaskDto.setOrderId(swapOrderId);
    }

    private Object[] createRowObject(TaskDto taskDto) {
        return new Object[]{
                taskDto.getName(),
                taskDto.getDescription(),
                String.format("%s %s", taskDto.getPerformer().getFirstName(), taskDto.getPerformer().getLastName()),
                taskDto.getOrderId(),
                taskDto.getExecutedAt()
        };
    }

    public int getTaskRowIndex(int taskId) {
        TaskDto foundTaskDto = tasks
                .stream()
                .filter(taskDto -> taskDto.getId().equals(taskId))
                .findFirst().orElseThrow(() -> new TaskNotFoundException(taskId));
        return tasks.indexOf(foundTaskDto);
    }
}

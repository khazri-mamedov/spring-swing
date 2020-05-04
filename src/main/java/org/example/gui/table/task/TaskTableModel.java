package org.example.gui.table.task;

import org.example.dto.PerformerDto;
import org.example.dto.TaskDto;
import org.example.gui.table.AbstractTableModel;

import java.awt.EventQueue;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;


public class TaskTableModel extends AbstractTableModel<Integer, TaskDto> {
    private static final long serialVersionUID = -658016799037210612L;

    public TaskTableModel() {
        super(new String[]{"Name", "Description", "Performer", "Order", "Executed At"});
    }

    public enum Column {
        NAME, DESCRIPTION, PERFORMER, ORDER, EXECUTED_AT,
    }

    public void updatePerformer(PerformerDto performerDto) {
        String performerName = performerDto.toString();
        getRowIndicesByPerformerId(performerDto.getId())
                .forEach(rowIndex -> EventQueue.invokeLater(() ->
                        setValueAt(performerName, rowIndex, Column.PERFORMER.ordinal())));
    }

    private Set<Integer> getRowIndicesByPerformerId(int id) {
        return container
                .stream()
                .filter(taskDto -> taskDto.getPerformer().getId() == id)
                .map(taskDto -> container.indexOf(taskDto))
                .collect(Collectors.toSet());
    }

    public void setValueAt(TaskDto taskDto, int rowIndex) {
        container.set(rowIndex, taskDto);

        PerformerDto performerDto = taskDto.getPerformer();
        String performerName = performerDto.toString();

        setValueAt(taskDto.getName(), rowIndex, Column.NAME.ordinal());
        setValueAt(taskDto.getDescription(), rowIndex, Column.DESCRIPTION.ordinal());
        setValueAt(performerName, rowIndex, Column.PERFORMER.ordinal());
        setValueAt(taskDto.getOrderId(), rowIndex, Column.ORDER.ordinal());
        setValueAt(taskDto.getExecutedAt(), rowIndex, Column.EXECUTED_AT.ordinal());
    }

    public void moveTasks(int firsSelectedTaskId, int secondSelectedTaskId) {
        LinkedList<TaskDto> taskDtos = getDtosById(firsSelectedTaskId, secondSelectedTaskId);
        if (taskDtos.size() == 2) {
            int firstSelectedRow = container.indexOf(taskDtos.getFirst());
            int secondSelectedRow = container.indexOf(taskDtos.getLast());
            swapOrderIds(taskDtos.getFirst(), taskDtos.getLast());
            Collections.swap(container, firstSelectedRow, secondSelectedRow);
            EventQueue.invokeLater(() -> {
                moveRow(firstSelectedRow, firstSelectedRow, secondSelectedRow);
                setValueAt(taskDtos.getFirst(), secondSelectedRow);
                setValueAt(taskDtos.getLast(), firstSelectedRow);
            });
        }
    }

    public void swapTasks(int firsSelectedTaskId, int secondSelectedTaskId) {
        // Normal code duplication. Multiple swap possibility and different logic
        LinkedList<TaskDto> taskDtos = getDtosById(firsSelectedTaskId, secondSelectedTaskId);
        if (taskDtos.size() == 2) {
            int firstSelectedRow = container.indexOf(taskDtos.getFirst());
            int secondSelectedRow = container.indexOf(taskDtos.getLast());
            swapOrderIds(taskDtos.getFirst(), taskDtos.getLast());
            Collections.swap(container, firstSelectedRow, secondSelectedRow);
            EventQueue.invokeLater(() -> {
                setValueAt(taskDtos.getFirst(), secondSelectedRow);
                setValueAt(taskDtos.getLast(), firstSelectedRow);
            });
        }
    }

    private void swapOrderIds(TaskDto prevTaskDto, TaskDto selectedTaskDto) {
        int swapOrderId = prevTaskDto.getOrderId();
        prevTaskDto.setOrderId(selectedTaskDto.getOrderId());
        selectedTaskDto.setOrderId(swapOrderId);
    }

    @Override
    protected Object[] createRowObject(TaskDto taskDto) {
        return new Object[]{
                taskDto.getName(),
                taskDto.getDescription(),
                taskDto.getPerformer(),
                taskDto.getOrderId(),
                taskDto.getExecutedAt()
        };
    }

    @Override
    protected void runBeforeInsert() {
        container = container
                .stream()
                .sorted(Comparator.comparingInt(TaskDto::getOrderId))
                .collect(Collectors.toList());
    }
}

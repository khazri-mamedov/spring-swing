package org.jazzteam.gui.table;

import lombok.Getter;
import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.dto.TaskDto;

import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class TaskTableModel extends DefaultTableModel {
    private static final long serialVersionUID = -658016799037210612L;

    @Getter
    private List<TaskDto> tasks = new LinkedList<>();

    public TaskTableModel() {
        super(new String[]{"Name", "Description", "Executor", "Order", "Executed At"},
                0);
    }

    public enum Column {
        NAME, DESCRIPTION, EXECUTOR, ORDER, EXECUTED_AT;
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

    public void setValueAt(TaskDto taskDto, int rowIndex) {
        ExecutorDto executorDto = taskDto.getExecutor();
        String executorName = String.format("%s %s", executorDto.getFirstName(), executorDto.getLastName());

        setValueAt(taskDto.getName(), rowIndex, Column.NAME.ordinal());
        setValueAt(taskDto.getDescription(), rowIndex, Column.DESCRIPTION.ordinal());
        setValueAt(executorName, rowIndex, Column.EXECUTOR.ordinal());
        setValueAt(taskDto.getOrderId(), rowIndex, Column.ORDER.ordinal());
        setValueAt(taskDto.getExecutedAt(), rowIndex, Column.EXECUTED_AT.ordinal());
    }

    @Override
    public void removeRow(int row) {
        tasks.remove(row);
        super.removeRow(row);
    }

    private Object[] createRowObject(TaskDto taskDto) {
        return new Object[]{
                taskDto.getName(),
                taskDto.getDescription(),
                String.format("%s %s", taskDto.getExecutor().getFirstName(), taskDto.getExecutor().getLastName()),
                taskDto.getOrderId(),
                taskDto.getExecutedAt()
        };
    }
}

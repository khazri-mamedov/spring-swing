package org.jazzteam.gui.table;

import lombok.Getter;
import org.jazzteam.dto.TaskDto;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;


public class TaskTableModel extends DefaultTableModel {
    private static final long serialVersionUID = -658016799037210612L;

    @Getter
    private List<TaskDto> tasks = new LinkedList<>();

    public TaskTableModel() {
        super(new String[]{"Name", "Description", "Executor", "Order", "Executed At"},
                0);
    }

    /**
     * Add row backed by container
     *
     * @param taskDto container's task
     */
    public void addRow(TaskDto taskDto) {
        tasks.add(taskDto);
        Object[] task = new Object[]{
                taskDto.getName(),
                taskDto.getDescription(),
                String.format("%s %s", taskDto.getExecutor().getFirstName(), taskDto.getExecutor().getLastName()),
                taskDto.getOrderId(),
                taskDto.getExecutedAt()
        };
        super.addRow(task);
    }

    @Override
    public void removeRow(int row) {
        tasks.remove(row);
        super.removeRow(row);
    }
}

package org.jazzteam.gui.table;

import lombok.Getter;

import javax.swing.JTable;

public class TaskTable extends JTable {
    // Avoids unnecessary down casting
    @Getter
    private TaskTableModel tableModel;

    public TaskTable(TaskTableModel taskTableModel) {
        super(taskTableModel);
        this.tableModel = taskTableModel;
    }
}

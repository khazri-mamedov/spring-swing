package org.jazzteam.gui.table;

import lombok.Getter;

import javax.swing.JTable;

public class TaskTable extends JTable {
    private static final long serialVersionUID = 3683949587378321148L;

    // Avoids unnecessary down casting
    @Getter
    private TaskTableModel tableModel;

    public TaskTable(TaskTableModel taskTableModel) {
        super(taskTableModel);
        this.tableModel = taskTableModel;
    }
}

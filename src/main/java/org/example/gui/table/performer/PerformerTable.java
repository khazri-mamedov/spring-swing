package org.example.gui.table.performer;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.JTable;

@NoArgsConstructor
public class PerformerTable extends JTable {

    @Getter
    private PerformerTableModel tableModel;

    public PerformerTable(PerformerTableModel tableModel) {
        super(tableModel);
        this.tableModel = tableModel;
    }
}

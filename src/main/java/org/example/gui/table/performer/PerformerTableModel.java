package org.example.gui.table.performer;

import org.example.dto.PerformerDto;
import org.example.gui.table.AbstractTableModel;

public class PerformerTableModel extends AbstractTableModel<Integer, PerformerDto> {
    private static final long serialVersionUID = 8162413249805960412L;

    public PerformerTableModel() {
        super(new String[]{"First Name", "Last Name"});
    }

    public enum Column {
        FIRST_NAME, LAST_NAME,
    }

    @Override
    protected Object[] createRowObject(PerformerDto performerDto) {
        return new Object[]{
                performerDto.getFirstName(),
                performerDto.getLastName()
        };
    }

    public void setValueAt(PerformerDto performerDto, int rowIndex) {
        container.set(rowIndex, performerDto);

        setValueAt(performerDto.getFirstName(), rowIndex, Column.FIRST_NAME.ordinal());
        setValueAt(performerDto.getLastName(), rowIndex, Column.LAST_NAME.ordinal());
    }
}

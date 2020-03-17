package org.jazzteam.gui.table.performer;

import lombok.Getter;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.dto.TaskDto;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;

public class PerformerTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 8162413249805960412L;

    @Getter
    private List<PerformerDto> performers = new LinkedList<>();

    public PerformerTableModel() {
        super(new String[]{"First Name", "Last Name"},
                0);
    }

    public enum Column {
        FIRST_NAME, LAST_NAME,
    }

    public void addRow(PerformerDto performerDto) {
        performers.add(performerDto);
        Object[] task = createRowObject(performerDto);
        super.addRow(task);
    }

    private Object[] createRowObject(PerformerDto performerDto) {
        return new Object[]{
                performerDto.getFirstName(),
                performerDto.getLastName()
        };
    }
}

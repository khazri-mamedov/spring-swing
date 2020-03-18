package org.jazzteam.gui.table.performer;

import lombok.Getter;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.exception.TaskNotFoundException;
import org.jazzteam.gui.table.task.TaskTableModel;

import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void removeRows(int... deletedPerformerIds) {
        Arrays.stream(deletedPerformerIds).forEach(this::removeRow);
    }

    @Override
    public void removeRow(int deletedPerformerId) {
        int rowIndex = getPerformerRowIndex(deletedPerformerId);
        performers.remove(rowIndex);
        super.removeRow(rowIndex);
    }

    public void insertRows(PerformerDto... performerDtos) {
        Arrays.stream(performerDtos).forEach(this::insertRow);
    }

    public void insertRow(PerformerDto performerDto) {
        performers.add(performerDto);
        int insertedIndex = performers.indexOf(performerDto);
        Object[] performer = createRowObject(performerDto);
        EventQueue.invokeLater(() -> insertRow(insertedIndex, performer));
    }

    public int getPerformerRowIndex(int performerId) {
        PerformerDto foundPerformerDto = performers
                .stream()
                .filter(performerDto -> performerDto.getId().equals(performerId))
                .findFirst().orElseThrow(() -> new TaskNotFoundException(performerId));
        return performers.indexOf(foundPerformerDto);
    }

    public void setValueAt(PerformerDto performerDto, int rowIndex) {
        performers.set(rowIndex, performerDto);

        setValueAt(performerDto.getFirstName(), rowIndex, Column.FIRST_NAME.ordinal());
        setValueAt(performerDto.getLastName(), rowIndex, Column.LAST_NAME.ordinal());
    }
}

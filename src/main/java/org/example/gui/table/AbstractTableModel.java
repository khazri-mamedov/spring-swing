package org.example.gui.table;

import lombok.Getter;
import org.example.dto.AbstractDto;
import org.example.gui.exception.DtoNotFoundException;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTableModel<ID, T extends AbstractDto<ID>> extends DefaultTableModel {
    private static final long serialVersionUID = -7594769076103636301L;

    @Getter
    protected List<T> container = new LinkedList<>();

    protected AbstractTableModel(String[] columns) {
        super(columns, 0);
    }

    public void addRow(T dto) {
        Object[] rowObject = createRowObject(dto);
        container.add(dto);
        super.addRow(rowObject);
    }

    public void insertRow(T dto) {
        container.add(dto);
        runBeforeInsert();
        int insertedIndex = container.indexOf(dto);
        insertRow(insertedIndex, createRowObject(dto));
    }

    public void insertRows(List<T> dtos) {
        dtos.forEach(this::insertRow);
    }

    public int getDtoRowIndex(int dtoId) {
        T foundDto = container
                .stream()
                .filter(dto -> dto.getId().equals(dtoId))
                .findFirst().orElseThrow(() -> new DtoNotFoundException(dtoId));
        return container.indexOf(foundDto);
    }

    public void removeRow(int deletedId) {
        int rowIndex = getDtoRowIndex(deletedId);
        container.remove(rowIndex);
        super.removeRow(rowIndex);
    }

    public void removeRows(int... deletedIds) {
        Arrays.stream(deletedIds).forEach(this::removeRow);
    }

    protected LinkedList<T> getDtosById(int firsSelectedId, int secondSelectedId) {
        return container
                .stream()
                .filter(taskDto ->
                        taskDto.getId().equals(firsSelectedId) || taskDto.getId().equals(secondSelectedId))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    protected abstract Object[] createRowObject(T dto);

    // Run custom logic
    protected void runBeforeInsert() {};
}

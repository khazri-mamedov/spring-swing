package org.jazzteam.gui.table.task;

import lombok.Getter;
import org.jazzteam.dto.PerformerDto;

import javax.swing.DefaultComboBoxModel;
import java.util.List;

public class PerformerComboBoxModel extends DefaultComboBoxModel<PerformerDto> {
    private static final long serialVersionUID = -681598724444896902L;

    @Getter
    List<PerformerDto> performers;

    private PerformerDto selectedPerformerDto;

    public PerformerComboBoxModel(List<PerformerDto> performers) {
        this.performers = performers;
    }

    public int getRowIndex(PerformerDto performerDto) {
        return performers.indexOf(performerDto);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedPerformerDto = (PerformerDto) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selectedPerformerDto;
    }

    @Override
    public int getSize() {
        return performers.size();
    }

    @Override
    public PerformerDto getElementAt(int index) {
        return performers.get(index);
    }

}

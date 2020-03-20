package org.jazzteam.gui.table.task;

import org.jazzteam.dto.PerformerDto;

import javax.swing.JComboBox;

public class PerformerBox extends JComboBox<PerformerDto> {
    public PerformerDto getSelectedPerformer() {
        // Avoids unnecessary casting in high level classes
        return (PerformerDto) getSelectedItem();
    }
}

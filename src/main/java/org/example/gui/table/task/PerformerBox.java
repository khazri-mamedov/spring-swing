package org.example.gui.table.task;

import org.example.dto.PerformerDto;

import javax.swing.JComboBox;

public class PerformerBox extends JComboBox<PerformerDto> {
    public PerformerDto getSelectedPerformer() {
        // Avoids unnecessary casting in high level classes
        return (PerformerDto) getSelectedItem();
    }
}

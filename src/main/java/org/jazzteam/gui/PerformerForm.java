package org.jazzteam.gui;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.gui.event.performer.CreateEvent;
import org.jazzteam.gui.event.performer.DeleteEvent;
import org.jazzteam.gui.event.performer.EditEvent;
import org.jazzteam.gui.table.performer.CreateModal;
import org.jazzteam.gui.table.performer.EditModal;
import org.jazzteam.gui.table.performer.PerformerTable;
import org.jazzteam.gui.table.performer.PerformerTableModel;
import org.jazzteam.gui.util.TableUtils;
import org.jazzteam.service.PerformerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// TODO create pair class one for GUI init. another for working with data (i.e GUI Designer)
@Component
@Lazy
@RequiredArgsConstructor
public class PerformerForm extends JFrame {

    private final MessageSource messageSource;
    private final PerformerService performerService;

    private JPanel headButtonsPanel;
    private JScrollPane performerTablePanel;
    private PerformerTable performerTable;
    private PerformerTableModel performerTableModel;

    private JButton createButton;
    private JButton deleteButton;
    private JButton editButton;

    private Locale defaultLocale = LocaleContextHolder.getLocale();

    @Autowired
    @Lazy
    private CreateModal performerCreateModal;

    @Autowired
    @Lazy
    private EditModal performerEditModal;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("performer.frame.layout", null, defaultLocale));
        setSize(600, 600);
        setLayout(new GridLayout(4, 1));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        headButtonsPanel = new JPanel();
        headButtonsPanel.setLayout(new FlowLayout());

        populateHeadButtonsPanel();
        populatePerformerTablePanel();

        add(headButtonsPanel);
        add(performerTablePanel);
    }

    @EventListener
    public void performerAdded(CreateEvent createEvent) {
        // Lazy Bean created because of event listener
        if (Objects.nonNull(performerTableModel)) {
            performerTableModel.insertRows(Collections.singletonList(createEvent.getSavedPerformerDto()));
        }
    }

    @EventListener
    public void performerDeleted(DeleteEvent deleteEvent) {
        // Lazy Bean created because of event listener
        if (Objects.nonNull(performerTableModel)) {
            int deletedTaskId = deleteEvent.getDeletedPerformerId();
            EventQueue.invokeLater(() -> performerTableModel.removeRows(deletedTaskId));
        }
    }

    @EventListener
    public void performerEdited(EditEvent editEvent) {
        // Lazy Bean created because of event listener
        if (Objects.nonNull(performerTableModel)) {
            final PerformerDto editedPerformerDto = editEvent.getEditedPerformerDto();
            int rowIndex = performerTableModel.getDtoRowIndex(editedPerformerDto.getId());
            EventQueue.invokeLater(() -> performerTableModel.setValueAt(editedPerformerDto, rowIndex));
        }
    }

    private void populatePerformerTablePanel() {
        performerTable = new PerformerTable();
        performerTablePanel = new JScrollPane(performerTable);
    }

    public void showDialog() {
        performerTableModel = new PerformerTableModel();
        List<PerformerDto> performers = performerService.getAll();
        performers.forEach(performerTableModel::addRow);
        performerTable.setModel(performerTableModel);

        setVisible(true);
    }

    private void populateHeadButtonsPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, defaultLocale));
        editButton = new JButton(messageSource.getMessage("edit.button", null, defaultLocale));
        deleteButton = new JButton(messageSource.getMessage("delete.button", null, defaultLocale));

        setCreateButtonListener();
        setEditButtonListener();
        setDeleteButtonListener();

        headButtonsPanel.add(createButton);
        headButtonsPanel.add(editButton);
        headButtonsPanel.add(deleteButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> performerCreateModal.showDialog());
    }

    private void setDeleteButtonListener() {
        deleteButton.addActionListener(event -> {
            int selectedRow = performerTable.getSelectedRow();
            if (TableUtils.isRowSelected(selectedRow)) {
                PerformerDto selectedPerformerDto = performerTableModel.getContainer().get(selectedRow);
                if (performerService.isDeletable(selectedPerformerDto.getId())) {
                    performerService.deleteSelected(selectedPerformerDto);
                    return;
                }
                final String deletable = messageSource.getMessage("non.deletable", null, defaultLocale);
                JOptionPane.showMessageDialog(null, deletable);
            }
        });
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> {
            int selectedRow = performerTable.getSelectedRow();
            if (TableUtils.isRowSelected(selectedRow)) {
                PerformerDto selectedPerformerDto = performerTableModel.getContainer().get(selectedRow);
                performerEditModal.showDialog(selectedPerformerDto, selectedRow);
            }
        });
    }
}

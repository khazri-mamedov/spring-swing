package org.jazzteam.gui;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.table.performer.PerformerTable;
import org.jazzteam.gui.table.performer.PerformerTableModel;
import org.jazzteam.service.PerformerService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;

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

    private Locale locale = LocaleContextHolder.getLocale();

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("performer.frame.layout", null, locale));
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

    private void populatePerformerTablePanel() {
        performerTable = new PerformerTable();
        performerTablePanel = new JScrollPane(performerTable);
    }

    public void showDialog() {
        performerTableModel = new PerformerTableModel();
        List<PerformerDto>  performers = performerService.getAllPerformers();
        performers.forEach(performerTableModel::addRow);
        performerTable.setModel(performerTableModel);

        setVisible(true);
    }

    private void populateHeadButtonsPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        editButton = new JButton(messageSource.getMessage("edit.button", null, locale));
        deleteButton = new JButton(messageSource.getMessage("delete.button", null, locale));

        setCreateButtonListener();
        setEditButtonListener();
        setDeleteButtonListener();

        headButtonsPanel.add(createButton);
        headButtonsPanel.add(editButton);
        headButtonsPanel.add(deleteButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> {});
    }

    private void setDeleteButtonListener() {
        deleteButton.addActionListener(event -> {

        });
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> {

        });
    }
}

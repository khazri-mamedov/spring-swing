package org.jazzteam.gui;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.table.CreateModal;
import org.jazzteam.gui.table.EditModal;
import org.jazzteam.gui.table.TaskTable;
import org.jazzteam.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MainForm extends JFrame {
    private final MessageSource messageSource;
    private final CreateModal createModal;
    private final EditModal editModal;
    private final TaskService taskService;

    private JPanel headButtonsPanel;
    private JScrollPane mainTablePanel;
    private TaskTable taskTable;

    private JButton createButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton upButton;
    private JButton downButton;

    private Locale locale = LocaleContextHolder.getLocale();

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("main.frame.layout", null, locale));
        setSize(600, 600);
        setLayout(new GridLayout(4, 1));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        headButtonsPanel = new JPanel();
        headButtonsPanel.setLayout(new FlowLayout());

        populateHeadButtonsPanel();
        populateMainTablePanel();

        add(headButtonsPanel);
        add(mainTablePanel);
    }

    private void populateHeadButtonsPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        editButton = new JButton(messageSource.getMessage("edit.button", null, locale));
        deleteButton = new JButton(messageSource.getMessage("delete.button", null, locale));
        upButton = new JButton(messageSource.getMessage("up.button", null, locale));
        downButton = new JButton(messageSource.getMessage("down.button", null, locale));

        setCreateButtonListener();
        setEditButtonListener();
        setDeleteButtonListener();

        headButtonsPanel.add(createButton);
        headButtonsPanel.add(editButton);
        headButtonsPanel.add(deleteButton);
        headButtonsPanel.add(upButton);
        headButtonsPanel.add(downButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> createModal.showDialog());
    }

    private void setDeleteButtonListener() {
        deleteButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (isRowSelected(selectedRow)) {
                taskService.deleteSelectedTask(taskTable.getSelectedRow());
            }
        });
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (isRowSelected(selectedRow)) {
                TaskDto selectedTaskDto = taskService.getSelectedTask(selectedRow);
                editModal.showDialog(selectedTaskDto, selectedRow);
            }
        });
    }

    private void populateMainTablePanel() {
        taskTable = new TaskTable(taskService.createAndPopulateTaskTableModel());
        mainTablePanel = new JScrollPane(taskTable);
    }

    private boolean isRowSelected(int selectedRow) {
        return selectedRow >= 0;
    }
}

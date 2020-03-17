package org.jazzteam.gui;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.SwapAction;
import org.jazzteam.gui.event.CreateEvent;
import org.jazzteam.gui.event.DeleteEvent;
import org.jazzteam.gui.event.EditEvent;
import org.jazzteam.gui.event.MoveEvent;
import org.jazzteam.gui.event.MoveEventType;
import org.jazzteam.gui.event.SwapEvent;
import org.jazzteam.gui.table.CreateModal;
import org.jazzteam.gui.table.EditModal;
import org.jazzteam.gui.table.TaskTable;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;

// TODO create pair class one for GUI init. another for working with data (i.e GUI Designer)
@Component
@RequiredArgsConstructor
public class MainForm extends JFrame {
    private static final long serialVersionUID = -246288357976232130L;

    private final MessageSource messageSource;
    private final CreateModal createModal;
    private final EditModal editModal;
    private final TaskService taskService;

    private JPanel headButtonsPanel;
    private JScrollPane mainTablePanel;
    private TaskTable taskTable;
    private TaskTableModel taskTableModel;

    private JButton createButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton upButton;
    private JButton downButton;
    private JButton swapButton;

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

    @EventListener
    public void taskAdded(CreateEvent createEvent) {
        taskTableModel.insertRows(createEvent.getSavedTaskDto());
    }

    @EventListener
    public void taskEdited(EditEvent editEvent) {
        final TaskDto editedTaskDto = editEvent.getEditedTaskDto();
        int rowIndex = taskTableModel.getTaskRowIndex(editedTaskDto.getId());
        EventQueue.invokeLater(() -> taskTableModel.setValueAt(editedTaskDto, rowIndex));
    }

    @EventListener
    public void taskDeleted(DeleteEvent deleteEvent) {
        int deletedTaskId = deleteEvent.getDeletedTaskId();
        EventQueue.invokeLater(() -> taskTableModel.removeRows(deletedTaskId));
    }

    @EventListener
    public void selectedRowChanged(MoveEvent moveEvent) {
        int selectedRow = taskTableModel.getTaskRowIndex(moveEvent.getFirstSelectedTaskId());
        int currentSelectedRow = taskTable.getSelectedRow();
        taskTableModel.moveTasks(moveEvent.getFirstSelectedTaskId(), moveEvent.getSecondSelectedTaskId());
        if (currentSelectedRow == selectedRow) {
            if (MoveEventType.UP.equals(moveEvent.getMoveEventType())) {
                taskTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
            } else if (MoveEventType.DOWN.equals(moveEvent.getMoveEventType())) {
                taskTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
            }
        }
    }

    @EventListener
    public void swappedTasks(SwapEvent swapEvent) {
        taskTableModel.swapTasks(swapEvent.getFirsSelectedTaskId(), swapEvent.getSecondSelectedTaskId());
    }

    private void populateHeadButtonsPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        editButton = new JButton(messageSource.getMessage("edit.button", null, locale));
        deleteButton = new JButton(messageSource.getMessage("delete.button", null, locale));
        upButton = new JButton(messageSource.getMessage("up.button", null, locale));
        downButton = new JButton(messageSource.getMessage("down.button", null, locale));
        swapButton = new JButton(messageSource.getMessage("swap.button", null, locale));

        setCreateButtonListener();
        setEditButtonListener();
        setDeleteButtonListener();
        setUpButtonListener();
        setDownButtonListener();
        setSwapButtonListener();

        headButtonsPanel.add(createButton);
        headButtonsPanel.add(editButton);
        headButtonsPanel.add(deleteButton);
        headButtonsPanel.add(upButton);
        headButtonsPanel.add(downButton);
        headButtonsPanel.add(swapButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> createModal.showDialog());
    }

    private void setDeleteButtonListener() {
        deleteButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (isRowSelected(selectedRow)) {
                TaskDto selectedTaskDto = taskTableModel.getTasks().get(selectedRow);
                taskService.deleteSelectedTask(selectedTaskDto);
            }
        });
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (isRowSelected(selectedRow)) {
                TaskDto selectedTaskDto = taskTableModel.getTasks().get(selectedRow);
                editModal.showDialog(selectedTaskDto, selectedRow);
            }
        });
    }

    private void setUpButtonListener() {
        upButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (isRowSelected(selectedRow) && !isFirstRow(selectedRow)) {
                final TaskDto firstSelectedTaskDto = taskTableModel.getTasks().get(selectedRow);
                final TaskDto secondSelectedTaskDto = taskTableModel.getTasks().get((selectedRow - 1));
                taskService.moveTask(firstSelectedTaskDto, secondSelectedTaskDto, MoveEventType.UP);
            }
        });
    }

    private void setDownButtonListener() {
        downButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (isRowSelected(selectedRow) && !isLastRow(selectedRow)) {
                final TaskDto firstSelectedTaskDto = taskTableModel.getTasks().get(selectedRow);
                final TaskDto secondSelectedTaskDto = taskTableModel.getTasks().get((selectedRow + 1));
                taskService.moveTask(firstSelectedTaskDto, secondSelectedTaskDto, MoveEventType.DOWN);
            }
        });
    }

    private void setSwapButtonListener() {
        swapButton.addActionListener(event -> {
            int[] selectedRows = taskTable.getSelectedRows();
            if (selectedRows.length == 2) {
                int firstRowIndex = selectedRows[0];
                int secondRowIndex = selectedRows[1];
                final TaskDto firstSelectedTaskDto = taskTableModel.getTasks().get(firstRowIndex);
                final TaskDto secondSelectedTaskDto = taskTableModel.getTasks().get((secondRowIndex));
                taskService.swapTasks(firstSelectedTaskDto, secondSelectedTaskDto);
            }
        });
    }

    private void populateMainTablePanel() {
        taskTableModel = new TaskTableModel();
        List<TaskDto> tasks = taskService.getAllTasks();
        tasks.forEach(taskTableModel::addRow);
        taskTable = new TaskTable(taskTableModel);
        mainTablePanel = new JScrollPane(taskTable);
    }

    private boolean isRowSelected(int selectedRow) {
        return selectedRow >= 0;
    }

    private boolean isFirstRow(int selectedRow) {
        return selectedRow < 1;
    }

    private boolean isLastRow(int selectedRow) {
        return selectedRow == taskTableModel.getTasks().size() - 1;
    }
}

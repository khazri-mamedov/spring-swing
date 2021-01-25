package org.example.gui;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskDto;
import org.example.gui.event.task.CreateEvent;
import org.example.gui.event.task.DeleteEvent;
import org.example.gui.event.task.EditEvent;
import org.example.gui.event.task.MoveEvent;
import org.example.gui.event.task.MoveEventType;
import org.example.gui.event.task.SwapEvent;
import org.example.gui.table.task.CreateModal;
import org.example.gui.table.task.EditModal;
import org.example.gui.table.task.TaskTable;
import org.example.gui.table.task.TaskTableModel;
import org.example.gui.util.TableUtils;
import org.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

// TODO create pair class one for GUI init. another for working with data (i.e GUI Designer)
@Component
@RequiredArgsConstructor
public class MainForm extends JFrame {
    private static final long serialVersionUID = -246288357976232130L;

    private final MessageSource messageSource;
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
    private JButton performerButton;

    @Autowired
    @Lazy
    private PerformerForm performerForm;

    @Autowired
    @Lazy
    private CreateModal taskCreateModal;

    @Autowired
    @Lazy
    private EditModal taskEditModal;

    private final Locale locale = LocaleContextHolder.getLocale();

    @PostConstruct
    private void initUi() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setTitle(messageSource.getMessage("main.frame.layout", null, locale));
        setSize(800, 800);
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
    public void performerEdited(org.example.gui.event.performer.EditEvent editEvent) {
        taskTableModel.updatePerformer(editEvent.getEditedPerformerDto());
    }

    @EventListener
    public void taskAdded(CreateEvent createEvent) {
        taskTableModel.insertRows(Collections.singletonList(createEvent.getSavedTaskDto()));
    }

    @EventListener
    public void taskEdited(EditEvent editEvent) {
        final TaskDto editedTaskDto = editEvent.getEditedTaskDto();
        int rowIndex = taskTableModel.getDtoRowIndex(editedTaskDto.getId());
        EventQueue.invokeLater(() -> taskTableModel.setValueAt(editedTaskDto, rowIndex));
    }

    @EventListener
    public void taskDeleted(DeleteEvent deleteEvent) {
        int deletedTaskId = deleteEvent.getDeletedTaskId();
        EventQueue.invokeLater(() -> taskTableModel.removeRows(deletedTaskId));
    }

    @EventListener
    public void selectedRowChanged(MoveEvent moveEvent) {
        int selectedRow = taskTableModel.getDtoRowIndex(moveEvent.getFirstSelectedTaskId());
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
        taskTableModel.swapTasks(swapEvent.getFirstSelectedTaskId(), swapEvent.getSecondSelectedTaskId());
    }

    private void populateHeadButtonsPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        editButton = new JButton(messageSource.getMessage("edit.button", null, locale));
        deleteButton = new JButton(messageSource.getMessage("delete.button", null, locale));
        upButton = new JButton(messageSource.getMessage("up.button", null, locale));
        downButton = new JButton(messageSource.getMessage("down.button", null, locale));
        swapButton = new JButton(messageSource.getMessage("swap.button", null, locale));
        performerButton = new JButton(messageSource.getMessage("performer.button", null, locale));

        setCreateButtonListener();
        setEditButtonListener();
        setDeleteButtonListener();
        setUpButtonListener();
        setDownButtonListener();
        setSwapButtonListener();
        setPerformerButtonListener();

        headButtonsPanel.add(createButton);
        headButtonsPanel.add(editButton);
        headButtonsPanel.add(deleteButton);
        headButtonsPanel.add(upButton);
        headButtonsPanel.add(downButton);
        headButtonsPanel.add(swapButton);
        headButtonsPanel.add(performerButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> taskCreateModal.showDialog());
    }

    private void setDeleteButtonListener() {
        deleteButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (TableUtils.isRowSelected(selectedRow)) {
                TaskDto selectedTaskDto = taskTableModel.getContainer().get(selectedRow);
                taskService.deleteSelected(selectedTaskDto);
            }
        });
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (TableUtils.isRowSelected(selectedRow)) {
                TaskDto selectedTaskDto = taskTableModel.getContainer().get(selectedRow);
                taskEditModal.showDialog(selectedTaskDto, selectedRow);
            }
        });
    }

    private void setUpButtonListener() {
        upButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (TableUtils.isRowSelected(selectedRow) && !TableUtils.isFirstRow(selectedRow)) {
                final TaskDto firstSelectedTaskDto = taskTableModel.getContainer().get(selectedRow);
                final TaskDto secondSelectedTaskDto = taskTableModel.getContainer().get((selectedRow - 1));
                taskService.moveTask(firstSelectedTaskDto, secondSelectedTaskDto, MoveEventType.UP);
            }
        });
    }

    private void setDownButtonListener() {
        downButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            if (TableUtils.isRowSelected(selectedRow) && !isLastRow(selectedRow)) {
                final TaskDto firstSelectedTaskDto = taskTableModel.getContainer().get(selectedRow);
                final TaskDto secondSelectedTaskDto = taskTableModel.getContainer().get((selectedRow + 1));
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
                final TaskDto firstSelectedTaskDto = taskTableModel.getContainer().get(firstRowIndex);
                final TaskDto secondSelectedTaskDto = taskTableModel.getContainer().get((secondRowIndex));
                taskService.swapTasks(firstSelectedTaskDto, secondSelectedTaskDto);
            }
        });
    }

    private void setPerformerButtonListener() {
        performerButton.addActionListener(event -> {
            performerForm.showDialog();
        });
    }

    private void populateMainTablePanel() {
        taskTableModel = new TaskTableModel();
        List<TaskDto> tasks = taskService.getAllTasksOrdered();
        tasks.forEach(taskTableModel::addRow);
        taskTable = new TaskTable(taskTableModel);
        mainTablePanel = new JScrollPane(taskTable);
    }

    private boolean isLastRow(int selectedRow) {
        return selectedRow == taskTableModel.getContainer().size() - 1;
    }
}

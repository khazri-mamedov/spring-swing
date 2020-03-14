package org.jazzteam.gui;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.DeleteAction;
import org.jazzteam.gui.action.TaskAction;
import org.jazzteam.gui.table.CreateModal;
import org.jazzteam.gui.table.EditModal;
import org.jazzteam.gui.table.TaskTable;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.repository.TaskRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MainForm extends JFrame {
    @Value("${message.broker.exchange.name}")
    private String exchangeName;

    private final MessageSource messageSource;
    private final CreateModal createModal;
    private final EditModal editModal;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AmqpTemplate amqpTemplate;
    private final ExecutorService executorService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private JPanel headButtonsPanel;
    private JScrollPane mainTablePanel;
    private TaskTable taskTable;

    private JButton createButton, deleteButton, editButton;

    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param taskAction execute logic
     */
    public void handleMessage(TaskAction taskAction) {
        taskAction.execute(taskTable, taskRepository, taskMapper, applicationEventPublisher);
    }

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

        setCreateButtonListener();
        setEditButtonListener();
        setDeleteButtonListener();

        headButtonsPanel.add(createButton);
        headButtonsPanel.add(editButton);
        headButtonsPanel.add(deleteButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> createModal.showDialog());
    }

    private void setDeleteButtonListener() {
        deleteButton.addActionListener(event -> deleteSelectedTask());
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> {
            int selectedRow = taskTable.getSelectedRow();
            TaskDto selectedTaskDto = getTaskByRow(selectedRow);
            editModal.showDialog(selectedTaskDto, selectedRow);
        });
    }

    private void deleteSelectedTask() {
        executorService.execute(() -> {
            int selectedRow = taskTable.getSelectedRow();
            TaskDto selectedTaskDto = getTaskByRow(selectedRow);
            TaskAction taskAction = new DeleteAction(selectedRow, selectedTaskDto.getId());
            taskRepository.deleteById(selectedTaskDto.getId());
            produceDeleteMessage(taskAction);
        });

    }

    private void produceDeleteMessage(TaskAction taskAction) {
        amqpTemplate.convertAndSend(exchangeName, "", taskAction);
    }

    private void populateMainTablePanel() {
        final TaskTableModel taskTableModel = new TaskTableModel(
                new String[]{"Name", "Description", "Executor", "Executed At"},
                0
        );

        List<TaskDto> tasks = taskRepository.findAll()
                .stream().map(taskMapper::toDto).collect(Collectors.toList());
        tasks.forEach(taskTableModel::addRow);

        taskTable = new TaskTable(taskTableModel);
        mainTablePanel = new JScrollPane(taskTable);
    }

    private TaskDto getTaskByRow(int row) {
        TaskTableModel tableModel = taskTable.getTableModel();
        return tableModel.getTasks().get(row);
    }
}

package org.jazzteam.gui.table;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.EditAction;
import org.jazzteam.gui.action.TaskAction;
import org.jazzteam.gui.event.DeleteEvent;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.model.TaskEntity;
import org.jazzteam.repository.TaskRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EditModal extends JDialog {
    @Value("${message.broker.exchange.name}")
    private String exchangeName;

    private final MessageSource messageSource;
    private final AmqpTemplate amqpTemplate;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ExecutorService executorService;

    private JButton editButton;
    private Locale locale = LocaleContextHolder.getLocale();
    private TaskDto selectedTaskDto;
    private int selectedRow;

    private JTextField nameField, executorField, descriptionField;

    @EventListener
    public void disposeIfDeleted(DeleteEvent deleteEvent) {
        if (Objects.nonNull(selectedTaskDto) && selectedTaskDto.getId() == deleteEvent.getDeletedTaskId()) {
            dispose();
        }
    }

    public void showDialog(TaskDto taskDto, int selectedRow) {
        this.selectedTaskDto = taskDto;
        this.selectedRow = selectedRow;

        setTitle(messageSource.getMessage("create.dialog.layout", null, locale));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        JPanel editFormPanel = new JPanel();
        editFormPanel.setLayout(new FlowLayout());


        populateEditFormPanel(editFormPanel);


        add(editFormPanel);
        setModal(true);
        setVisible(true);
    }

    private void populateEditFormPanel(JPanel editFormPanel) {
        editButton = new JButton(messageSource.getMessage("edit.button", null, locale));

        nameField = new JTextField(30);
        descriptionField = new JTextField(30);
        executorField = new JTextField(30);

        nameField.setText(selectedTaskDto.getName());
        descriptionField.setText(selectedTaskDto.getDescription());
        executorField.setText(selectedTaskDto.getExecutor().getId().toString());

        setEditButtonListener();

        editFormPanel.add(nameField);
        editFormPanel.add(descriptionField);
        editFormPanel.add(executorField);
        editFormPanel.add(editButton);
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> editTask());
    }

    private void editTask() {
        selectedTaskDto.setName(nameField.getText());
        selectedTaskDto.setDescription(descriptionField.getText());
        selectedTaskDto.getExecutor().setId(Integer.parseInt(executorField.getText()));
        executorService.execute(() -> {
            TaskEntity updatedTaskEntity = taskRepository.save(taskMapper.toEntity(selectedTaskDto));
            TaskDto updatedTaskDto = taskMapper.toDto(updatedTaskEntity);
            TaskAction taskAction = new EditAction(updatedTaskDto, selectedRow);
            produceEditMessage(taskAction);
        });
        dispose();
    }

    private void produceEditMessage(TaskAction taskAction) {
        amqpTemplate.convertAndSend(exchangeName, "", taskAction);
    }
}

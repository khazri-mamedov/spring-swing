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

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
public class EditModal extends JDialog {
    @Value("${message.broker.exchange.name}")
    private String exchangeName;

    private final MessageSource messageSource;
    private final AmqpTemplate amqpTemplate;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ExecutorService executorService;

    private JButton editButton;
    private Locale defaultLocale;
    private TaskDto selectedTaskDto;
    private int selectedRow;

    private JTextField nameField;
    private JTextField executorField;
    private JTextField descriptionField;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("create.dialog.layout", null, defaultLocale));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        defaultLocale = LocaleContextHolder.getLocale();
        JPanel editFormPanel = new JPanel(new FlowLayout());

        editButton = new JButton(messageSource.getMessage("edit.button", null, defaultLocale));
        nameField = new JTextField(30);
        descriptionField = new JTextField(30);
        executorField = new JTextField(30);

        editFormPanel.add(nameField);
        editFormPanel.add(descriptionField);
        editFormPanel.add(executorField);
        editFormPanel.add(editButton);
        setEditButtonListener();
        add(editFormPanel);
        setModal(true);
    }

    @EventListener
    public void disposeIfDeleted(DeleteEvent deleteEvent) {
        if (Objects.nonNull(selectedTaskDto) && selectedTaskDto.getId() == deleteEvent.getDeletedTaskId()) {
            final String closed = messageSource.getMessage("edit.closed.delete", null, defaultLocale);
            JOptionPane.showMessageDialog(null, closed);
            dispose();
        }
    }

    public void showDialog(TaskDto taskDto, int selectedRow) {
        this.selectedTaskDto = taskDto;
        this.selectedRow = selectedRow;

        populateEditFormPanel();
        setVisible(true);
    }


    private void populateEditFormPanel() {
        nameField.setText(selectedTaskDto.getName());
        descriptionField.setText(selectedTaskDto.getDescription());
        executorField.setText(selectedTaskDto.getExecutor().getId().toString());
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

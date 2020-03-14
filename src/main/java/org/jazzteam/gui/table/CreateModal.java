package org.jazzteam.gui.table;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.CreateAction;
import org.jazzteam.gui.action.TaskAction;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.model.TaskEntity;
import org.jazzteam.repository.TaskRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CreateModal extends JDialog {
    @Value("${message.broker.exchange.name}")
    private String exchangeName;

    private final MessageSource messageSource;
    private final AmqpTemplate amqpTemplate;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ExecutorService executorService;

    private JPanel createFormPanel;
    private JButton createButton;
    private Locale locale = LocaleContextHolder.getLocale();

    private JTextField nameField, executorField, descriptionField;

    public void showDialog() {
        setTitle(messageSource.getMessage("create.dialog.layout", null, locale));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        createFormPanel = new JPanel();
        createFormPanel.setLayout(new FlowLayout());

        populateCreateFormPanel();

        add(createFormPanel);
        setModal(true);
        setVisible(true);
    }

    private void populateCreateFormPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));

        nameField = new JTextField(30);
        descriptionField = new JTextField(30);
        executorField = new JTextField(30);

        setCreateButtonListener();

        createFormPanel.add(nameField);
        createFormPanel.add(descriptionField);
        createFormPanel.add(executorField);
        createFormPanel.add(createButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> {
            addNewTask();
        });
    }

    private void addNewTask() {
        executorService.execute(() -> {
            TaskDto taskDto = new TaskDto();
            taskDto.setName(nameField.getText());
            taskDto.setDescription(descriptionField.getText());
            ExecutorDto executorDto = new ExecutorDto();
            executorDto.setId(Integer.parseInt(executorField.getText()));
            taskDto.setExecutor(executorDto);
            taskDto.setExecutedAt(LocalDate.now());
            TaskEntity savedTaskEntity = taskRepository.save(taskMapper.toEntity(taskDto));
            TaskAction taskAction = new CreateAction(savedTaskEntity.getId());
            produceCreateMessage(taskAction);
        });
        dispose();
    }

    private void produceCreateMessage(TaskAction taskAction) {
        amqpTemplate.convertAndSend(exchangeName, "", taskAction);
    }
}

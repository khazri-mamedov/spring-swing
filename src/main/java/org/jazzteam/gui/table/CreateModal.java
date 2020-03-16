package org.jazzteam.gui.table;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class CreateModal extends JDialog {
    private final MessageSource messageSource;
    private final TaskService taskService;

    private JPanel createFormPanel;
    private JButton createButton;
    private Locale locale = LocaleContextHolder.getLocale();

    private JTextField nameField;
    private JTextField executorField;
    private JTextField descriptionField;
    private JTextField orderField;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("create.dialog.layout", null, locale));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        createFormPanel = new JPanel();
        createFormPanel.setLayout(new FlowLayout());

        populateCreateFormPanel();

        add(createFormPanel);
        setModal(true);
    }

    public void showDialog() {
        // JTextField is singleton (performance)
        nameField.setText("");
        descriptionField.setText("");
        executorField.setText("");
        orderField.setText("");
        setVisible(true);
    }

    private void populateCreateFormPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        nameField = new JTextField(30);
        descriptionField = new JTextField(30);
        executorField = new JTextField(30);
        orderField = new JTextField(30);

        setCreateButtonListener();

        createFormPanel.add(nameField);
        createFormPanel.add(descriptionField);
        createFormPanel.add(executorField);
        createFormPanel.add(orderField);
        createFormPanel.add(createButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> addNewTask());
    }

    private void addNewTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName(nameField.getText());
        taskDto.setDescription(descriptionField.getText());
        ExecutorDto executorDto = new ExecutorDto();
        executorDto.setId(Integer.parseInt(executorField.getText().trim()));
        taskDto.setExecutor(executorDto);
        taskDto.setOrderId(Integer.parseInt(orderField.getText().trim()));
        taskDto.setExecutedAt(LocalDate.now());
        taskService.createTask(taskDto);
        dispose();
    }
}

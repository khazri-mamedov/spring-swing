package org.jazzteam.gui.table.task;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Locale;

@RequiredArgsConstructor
@Component("taskCreateModal")
@Lazy
public class CreateModal extends JDialog {
    private static final long serialVersionUID = 8957483143302457227L;

    private final MessageSource messageSource;
    private final TaskService taskService;

    private JPanel createFormPanel;
    private JButton createButton;
    private Locale locale = LocaleContextHolder.getLocale();

    private JTextField nameField;
    private JTextField performerField;
    private JTextField descriptionField;
    private JTextField orderField;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("create.task.dialog.layout", null, locale));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        createFormPanel = new JPanel();
        createFormPanel.setLayout(new GridLayout(5, 2));

        populateCreateFormPanel();

        add(createFormPanel);
        pack();
        setModal(true);
    }

    public void showDialog() {
        // JTextField is singleton (performance)
        nameField.setText("");
        descriptionField.setText("");
        performerField.setText("");
        orderField.setText("");
        setVisible(true);
    }

    private void populateCreateFormPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        nameField = new JTextField(30);
        descriptionField = new JTextField(30);
        performerField = new JTextField(30);
        orderField = new JTextField(30);

        setCreateButtonListener();

        createFormPanel.add(new JLabel(messageSource.getMessage("name.label", null, locale)));
        createFormPanel.add(nameField);
        createFormPanel.add(new JLabel(messageSource.getMessage("description.label", null, locale)));
        createFormPanel.add(descriptionField);
        createFormPanel.add(new JLabel(messageSource.getMessage("performer.label", null, locale)));
        createFormPanel.add(performerField);
        createFormPanel.add(new JLabel(messageSource.getMessage("order.label", null, locale)));
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
        PerformerDto performerDto = new PerformerDto();
        performerDto.setId(Integer.parseInt(performerField.getText().trim()));
        taskDto.setPerformer(performerDto);
        taskDto.setOrderId(Integer.parseInt(orderField.getText().trim()));
        taskDto.setExecutedAt(LocalDate.now());
        taskService.createTask(taskDto);
        dispose();
    }
}

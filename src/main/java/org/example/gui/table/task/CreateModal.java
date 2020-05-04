package org.example.gui.table.task;

import lombok.RequiredArgsConstructor;
import org.example.dto.PerformerDto;
import org.example.dto.TaskDto;
import org.example.gui.event.performer.EditEvent;
import org.example.service.PerformerService;
import org.example.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Component("taskCreateModal")
@Lazy
public class CreateModal extends JDialog {
    private static final long serialVersionUID = 8957483143302457227L;

    private final MessageSource messageSource;
    private final TaskService taskService;
    private final PerformerService performerService;

    private JPanel createFormPanel;
    private JButton createButton;
    private Locale locale = LocaleContextHolder.getLocale();

    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField orderField;
    private PerformerBox performerBox;

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

    @EventListener
    public void performerEdited(EditEvent editEvent) {
        if (Objects.nonNull(performerBox)) {
            final int rowIndex = performerBox.getSelectedIndex();
            final PerformerComboBoxModel performerComboBoxModelUpdated
                    = new PerformerComboBoxModel(performerService.getAllOrdered(Sort.by("id")));
            performerBox.setModel(performerComboBoxModelUpdated);
            performerBox.setSelectedIndex(rowIndex);
        }
    }

    public void showDialog() {
        // JTextField is singleton (performance)
        nameField.setText("");
        descriptionField.setText("");
        orderField.setText("");

        populatePerformers();

        setVisible(true);
    }

    private void populateCreateFormPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        nameField = new JTextField(30);
        descriptionField = new JTextField(30);
        orderField = new JTextField(30);
        performerBox = new PerformerBox();

        setCreateButtonListener();

        createFormPanel.add(new JLabel(messageSource.getMessage("name.label", null, locale)));
        createFormPanel.add(nameField);
        createFormPanel.add(new JLabel(messageSource.getMessage("description.label", null, locale)));
        createFormPanel.add(descriptionField);
        createFormPanel.add(new JLabel(messageSource.getMessage("performer.label", null, locale)));
        createFormPanel.add(performerBox);
        createFormPanel.add(new JLabel(messageSource.getMessage("order.label", null, locale)));
        createFormPanel.add(orderField);
        createFormPanel.add(createButton);
    }

    private void populatePerformers() {
        List<PerformerDto> performerDtos = performerService.getAllOrdered(Sort.by("id"));
        PerformerComboBoxModel performerComboBoxModel = new PerformerComboBoxModel(performerDtos);
        performerBox.setModel(performerComboBoxModel);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> {
            if (Objects.nonNull(performerBox.getSelectedPerformer())) {
                addNewTask();
            }
        });
    }

    private void addNewTask() {
        PerformerDto performerDto = performerBox.getSelectedPerformer();
        TaskDto taskDto = new TaskDto();
        taskDto.setName(nameField.getText());
        taskDto.setDescription(descriptionField.getText());
        taskDto.setPerformer(performerDto);
        taskDto.setOrderId(Integer.parseInt(orderField.getText().trim()));
        taskDto.setExecutedAt(LocalDate.now());
        taskService.create(taskDto);
        dispose();
    }
}

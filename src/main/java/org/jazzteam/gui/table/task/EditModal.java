package org.jazzteam.gui.table.task;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.task.DeleteEvent;
import org.jazzteam.gui.event.task.EditEvent;
import org.jazzteam.gui.event.task.MoveEvent;
import org.jazzteam.gui.util.TableUtils;
import org.jazzteam.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Component("taskEditModal")
@Lazy
public class EditModal extends JDialog {
    private static final long serialVersionUID = -4874705556978842092L;

    private final MessageSource messageSource;
    private final TaskService taskService;

    private JButton editButton;
    private Locale defaultLocale;
    private TaskDto selectedTaskDto;
    private int selectedRow;

    private JTextField nameField;
    private JTextField executorField;
    private JTextField descriptionField;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("edit.dialog.layout", null, defaultLocale));
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
        if (Objects.nonNull(selectedTaskDto) && (selectedTaskDto.getId() == deleteEvent.getDeletedTaskId())) {
            TableUtils.disposeIfDeleted(this, messageSource);
        }
    }

    @EventListener
    public void notifyAndUpdate(EditEvent editEvent) {
        TaskDto editedTaskDto = editEvent.getEditedTaskDto();
        if (Objects.nonNull(selectedTaskDto)
                && (selectedTaskDto.getId().equals(editedTaskDto.getId()))) {
            TableUtils.showNotification(messageSource, "edited.before");
            setFields(editedTaskDto);
        }
    }

    @EventListener
    public void tableStructureChanged(MoveEvent moveEvent) {
        if (Objects.nonNull(selectedTaskDto)) {
            final String structureChanged
                    = messageSource.getMessage("move.structure.changed", null, defaultLocale);
            JOptionPane.showMessageDialog(null, structureChanged);
            dispose();
        }
    }

    @Override
    public void dispose() {
        // Singleton component needs null out state
        selectedTaskDto = null;
        super.dispose();
    }

    public void showDialog(TaskDto taskDto, int selectedRow) {
        this.selectedTaskDto = taskDto;
        this.selectedRow = selectedRow;

        populateEditFormPanel();
        pack();
        setVisible(true);
    }


    private void populateEditFormPanel() {
        setFields(selectedTaskDto);
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> editTask());
    }

    private void editTask() {
        selectedTaskDto.setName(nameField.getText());
        selectedTaskDto.setDescription(descriptionField.getText());
        selectedTaskDto.getPerformer().setId(Integer.parseInt(executorField.getText().trim()));
        taskService.update(selectedTaskDto);
        dispose();
    }

    private void setFields(TaskDto taskDto) {
        nameField.setText(taskDto.getName());
        descriptionField.setText(taskDto.getDescription());
        executorField.setText(taskDto.getPerformer().getId().toString());
    }
}

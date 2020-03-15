package org.jazzteam.gui.table;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.DeleteEvent;
import org.jazzteam.gui.event.EditEvent;
import org.jazzteam.gui.event.MoveEvent;
import org.jazzteam.service.TaskService;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class EditModal extends JDialog {

    private final MessageSource messageSource;
    private final TaskService taskService;

    private JButton editButton;
    private Locale defaultLocale;
    private TaskDto selectedTaskDto;
    private int selectedRow;

    private JTextField nameField;
    private JTextField executorField;
    private JTextField descriptionField;
    //private JTextField orderField;

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
        //orderField = new JTextField(30);

        editFormPanel.add(nameField);
        editFormPanel.add(descriptionField);
        editFormPanel.add(executorField);
        //editFormPanel.add(orderField);
        editFormPanel.add(editButton);
        setEditButtonListener();
        add(editFormPanel);
        setModal(true);
    }

    @EventListener
    public void disposeIfDeleted(DeleteEvent deleteEvent) {
        if (Objects.nonNull(selectedTaskDto) && (selectedTaskDto.getId() == deleteEvent.getDeletedTaskId())) {
            final String closed = messageSource.getMessage("edit.closed.delete", null, defaultLocale);
            JOptionPane.showMessageDialog(null, closed);
            dispose();
        }
    }

    @EventListener
    public void notifyAndUpdate(EditEvent editEvent) {
        TaskDto editedTaskDto = editEvent.getEditedTaskDto();
        if (Objects.nonNull(selectedTaskDto)
                && (selectedTaskDto.getId().equals(editedTaskDto.getId()))) {
            final String edited = messageSource.getMessage("edited.before", null, defaultLocale);
            JOptionPane.showMessageDialog(null, edited);
            setFields(editedTaskDto);
        }
    }

    @EventListener
    public void tableStructureChanged(MoveEvent moveEvent) {
        if(Objects.nonNull(selectedTaskDto)) {
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
        selectedTaskDto.getExecutor().setId(Integer.parseInt(executorField.getText().trim()));
        //selectedTaskDto.setOrderId(Integer.parseInt(orderField.getText().trim()));
        taskService.updateTask(selectedTaskDto, selectedRow);
        dispose();
    }

    private void setFields(TaskDto taskDto) {
        nameField.setText(taskDto.getName());
        descriptionField.setText(taskDto.getDescription());
        executorField.setText(taskDto.getExecutor().getId().toString());
        //orderField.setText(taskDto.getOrderId().toString());
    }
}

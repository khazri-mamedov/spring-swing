package org.jazzteam.gui.table.performer;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.event.performer.DeleteEvent;
import org.jazzteam.gui.event.performer.EditEvent;
import org.jazzteam.gui.util.TableUtils;
import org.jazzteam.service.PerformerService;
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
@Component("performerEditModal")
@Lazy
public class EditModal extends JDialog {
    private final MessageSource messageSource;
    private final PerformerService performerService;

    private JButton editButton;
    private Locale defaultLocale;
    private PerformerDto selectedPerformerDto;
    private int selectedRow;

    private JTextField firstNameField;
    private JTextField lastNameField;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("edit.dialog.layout", null, defaultLocale));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        defaultLocale = LocaleContextHolder.getLocale();
        JPanel editFormPanel = new JPanel(new FlowLayout());

        editButton = new JButton(messageSource.getMessage("edit.button", null, defaultLocale));
        firstNameField = new JTextField(30);
        lastNameField = new JTextField(30);

        editFormPanel.add(firstNameField);
        editFormPanel.add(lastNameField);
        editFormPanel.add(editButton);

        setEditButtonListener();
        add(editFormPanel);
        setModal(true);
    }

    @Override
    public void dispose() {
        // Singleton component needs null out state
        selectedPerformerDto = null;
        super.dispose();
    }

    @EventListener
    public void disposeIfDeleted(DeleteEvent deleteEvent) {
        if (Objects.nonNull(selectedPerformerDto)
                && (selectedPerformerDto.getId() == deleteEvent.getDeletedPerformerId())) {
            TableUtils.disposeIfDeleted(this, messageSource);
        }
    }

    @EventListener
    public void notifyAndUpdate(EditEvent editEvent) {
        PerformerDto editedPerformerDto = editEvent.getEditedPerformerDto();
        if (Objects.nonNull(selectedPerformerDto)
                && (selectedPerformerDto.getId().equals(editedPerformerDto.getId()))) {
            TableUtils.showNotification(messageSource, "edited.before");
            setFields(editedPerformerDto);
        }
    }

    public void showDialog(PerformerDto performerDto, int selectedRow) {
        this.selectedPerformerDto = performerDto;
        this.selectedRow = selectedRow;

        populateEditFormPanel();
        pack();
        setVisible(true);
    }

    private void populateEditFormPanel() {
        setFields(selectedPerformerDto);
    }

    private void setFields(PerformerDto performerDto) {
        firstNameField.setText(performerDto.getFirstName());
        lastNameField.setText(performerDto.getLastName());
    }

    private void setEditButtonListener() {
        editButton.addActionListener(event -> editPerformer());
    }

    private void editPerformer() {
        selectedPerformerDto.setFirstName(firstNameField.getText());
        selectedPerformerDto.setLastName(lastNameField.getText());

        performerService.update(selectedPerformerDto);

        dispose();
    }
}

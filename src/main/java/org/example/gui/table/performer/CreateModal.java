package org.example.gui.table.performer;

import lombok.RequiredArgsConstructor;
import org.example.dto.PerformerDto;
import org.example.service.PerformerService;
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
import java.util.Locale;

@RequiredArgsConstructor
@Component("performerCreateModal")
@Lazy
public class CreateModal extends JDialog {

    private final MessageSource messageSource;
    private final PerformerService performerService;

    private JPanel createFormPanel;
    private JButton createButton;
    private Locale locale = LocaleContextHolder.getLocale();

    private JTextField firstNameField;
    private JTextField lastNameField;

    @PostConstruct
    private void initUi() {
        setTitle(messageSource.getMessage("create.performer.dialog.layout", null, locale));
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
        firstNameField.setText("");
        lastNameField.setText("");
        setVisible(true);
    }

    private void populateCreateFormPanel() {
        createButton = new JButton(messageSource.getMessage("create.button", null, locale));
        firstNameField = new JTextField(30);
        lastNameField = new JTextField(30);

        setCreateButtonListener();

        createFormPanel.add(new JLabel(messageSource.getMessage("first.name.label", null, locale)));
        createFormPanel.add(firstNameField);
        createFormPanel.add(new JLabel(messageSource.getMessage("last.name.label", null, locale)));
        createFormPanel.add(lastNameField);
        createFormPanel.add(createButton);
    }

    private void setCreateButtonListener() {
        createButton.addActionListener(event -> addNewPerformer());
    }

    private void addNewPerformer() {
        PerformerDto performerDto = new PerformerDto();
        performerDto.setFirstName(firstNameField.getText());
        performerDto.setLastName(lastNameField.getText());
        ;

        performerService.create(performerDto);
        dispose();
    }
}

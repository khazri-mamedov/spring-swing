package org.jazzteam.gui.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.swing.JOptionPane;
import java.awt.Dialog;

public final class TableUtils {
    private TableUtils() {
        throw new AssertionError();
    }

    public static boolean isRowSelected(int selectedRow) {
        return selectedRow >= 0;
    }

    public static boolean isFirstRow(int selectedRow) {
        return selectedRow < 1;
    }

    public static void disposeIfDeleted(Dialog dialog, MessageSource messageSource) {
        final String closed
                = messageSource.getMessage("edit.closed.delete", null, LocaleContextHolder.getLocale());
        JOptionPane.showMessageDialog(null, closed);
        dialog.dispose();
    }
}

package org.pseudonymous.plagiarism.components;

import org.pseudonymous.plagiarism.config.Configs;
import org.pseudonymous.plagiarism.gui.Resources;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Dialogs {

    public static JFrame createBaseDialog() {
        final JFrame dialog = new JFrame(Configs.appName);
        dialog.setLocationRelativeTo(null);
        dialog.setAutoRequestFocus(true);
        dialog.setFocusable(true);
        dialog.setIconImage(Resources.getResource(Configs.iconName));
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        dialog.invalidate();
        return dialog;
    }

    public static void BaseDialog(String title, String message, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    public static String FileChooserDialog(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public static void ErrorDialog(String title, String message) {
        BaseDialog(title, message, JOptionPane.ERROR_MESSAGE);
    }

}
package org.pseudonymous.plagiarism.gui;

import org.pseudonymous.plagiarism.components.Dialogs;
import org.pseudonymous.plagiarism.config.Configs;
import org.pseudonymous.plagiarism.config.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pseudonymous
 */
public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel titleText, splashImage;
    private JButton optionsButton, startButton;

    public interface OnPress {
        void onStartPressed(String folder);
    }

    private OnPress onPress = null;

    public GUI() {
        changeLookAndFeel();

        titleText = new JLabel("Plagiarism Checker", SwingConstants.CENTER);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY, Font.DIALOG);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        attributes.put(TextAttribute.SIZE, 26);
        titleText.setFont(Font.getFont(attributes));
        splashImage = new JLabel();

        JPanel leftPanel = new JPanel();

        GroupLayout leftLayout = new GroupLayout(leftPanel);
        leftLayout.setAutoCreateGaps(true);
        leftLayout.setAutoCreateContainerGaps(true);
        leftLayout.setHorizontalGroup(leftLayout.createSequentialGroup()
                .addGroup(leftLayout.createParallelGroup()
                        .addComponent(titleText)
                        .addComponent(splashImage)));
        leftLayout.setVerticalGroup(leftLayout.createSequentialGroup()
                .addComponent(titleText)
                .addComponent(splashImage));
        leftPanel.setLayout(leftLayout);
        leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleText, GroupLayout.Alignment.CENTER);
        leftPanel.add(splashImage, GroupLayout.Alignment.CENTER);

        Image logoImg = Resources.getResource(Configs.iconName);
        if (logoImg != null) {
            splashImage.setIcon(new ImageIcon(logoImg));
        }

        optionsButton = new JButton("Options");
        optionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsButton.setMinimumSize(new Dimension(150, 20));

        startButton = new JButton("Start");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMinimumSize(new Dimension(150, 20));
        startButton.addActionListener(actionEvent -> {
            Logger.Log("Start button pressed");
            Logger.Log("Opening the file chooser dialog");
            String selectedFolder = Dialogs.FileChooserDialog("Choose the folder to scan");
            Logger.Log("The user selected the folder " + selectedFolder);
            if (onPress != null) {
                onPress.onStartPressed(selectedFolder);
            }
        });

        JPanel buttonPanel = new JPanel();

        GroupLayout rightLayout = new GroupLayout(buttonPanel);
        rightLayout.setAutoCreateGaps(true);
        rightLayout.setAutoCreateContainerGaps(true);
        rightLayout.setHorizontalGroup(rightLayout.createSequentialGroup()
                .addGroup(rightLayout.createParallelGroup()
                        .addComponent(optionsButton)
                        .addComponent(startButton)));
        rightLayout.setVerticalGroup(rightLayout.createSequentialGroup()
                .addComponent(optionsButton)
                .addComponent(startButton));

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setLayout(rightLayout);
        buttonPanel.add(optionsButton, GroupLayout.Alignment.CENTER);
        buttonPanel.add(startButton, GroupLayout.Alignment.CENTER);
        buttonPanel.setMinimumSize(new Dimension(200, 100));

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(leftPanel);
        panel.add(buttonPanel);

        frame = new JFrame(Configs.appName);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(Configs.windowX, Configs.windowY);
        frame.setIconImage(logoImg);
        frame.setResizable(false);
    }

    public void setOnPressListener(OnPress onPress) {
        this.onPress = onPress;
    }

    public void start() {
        frame.pack();
        frame.setVisible(true);
    }

    private static void changeLookAndFeel() {
        /*
         * Change the look and feel
		 */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException err) {
            err.printStackTrace();
            Dialogs.ErrorDialog("Copernicus Look and Feel Error",
                    "Copernicus couldn't change the Look and Feel of the dashboard");
        }
    }
}

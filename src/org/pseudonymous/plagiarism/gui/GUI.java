package org.pseudonymous.plagiarism.gui;

import org.apache.commons.lang3.tuple.Pair;
import org.pseudonymous.plagiarism.components.Dialogs;
import org.pseudonymous.plagiarism.components.DoubleJSlider;
import org.pseudonymous.plagiarism.config.*;
import org.pseudonymous.plagiarism.processing.Processor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pseudonymous
 */
public class GUI {
    private JFrame frame;
    private JPanel leftPanel;
    private JPanel progressPanel;
    private JLabel progressText;
    private JProgressBar progressBar;
    private JButton stopButton;
    private Image logoImg;

    public interface OnPressListener {
        void onStartPressed(String folder, String parentFolder);

        void onOptionsPressed();

        void onStopPressed();
    }

    private OnPressListener onPress = null;

    public GUI() {
        changeLookAndFeel();

        JLabel titleText = new JLabel("Plagiarism Checker", SwingConstants.CENTER);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY, Font.DIALOG);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        attributes.put(TextAttribute.SIZE, 26);
        titleText.setFont(Font.getFont(attributes));
        JLabel splashImage = new JLabel();

        leftPanel = new JPanel();

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

        logoImg = Resources.getResource(Configs.iconName);
        if (logoImg != null) {
            splashImage.setIcon(new ImageIcon(logoImg));
        }

        JButton optionsButton = new JButton("Options");
        optionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsButton.setMinimumSize(new Dimension(150, 20));
        optionsButton.addActionListener(actionEvent -> {
            Logger.Log("Options button pressed");
            if (onPress != null) {
                onPress.onOptionsPressed();
            } else Logger.Log("Failed to handle options button pressed! The handler is null!", true);
        });

        JButton startButton = new JButton("Start");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMinimumSize(new Dimension(150, 20));
        startButton.addActionListener(actionEvent -> {
            Logger.Log("Start button pressed");
            Logger.Log("Opening the file chooser dialog");
            String selectedFolder = Dialogs.FileChooserDialog("Choose the folder to scan");
            if (selectedFolder == null) {
                Logger.Log("The user didn't select a proper folder", true);
                Dialogs.ErrorDialog(Configs.appName + " | No folder selected", "Exiting... no folder selected!");
                System.exit(0);
            }
            Logger.Log("The user selected the folder " + selectedFolder);
            Logger.Log("Opening the confirmation box for parent folder selection");
            String parentFolder = null;
            if (Dialogs.ConfirmDialog(Configs.appName + " | Select parent folder", "Would you like to select a folder containing sample or instruction document's?")) {
                parentFolder = Dialogs.FileChooserDialog("Choose the parent folder to scan");
                Logger.Log("The user selected the parent folder + " + parentFolder);
            }
            if (onPress != null) {
                onPress.onStartPressed(selectedFolder, parentFolder);
            } else Logger.Log("Failed to handle start button pressed! The handler is null!", true);
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

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(leftPanel);
        panel.add(buttonPanel);

        stopButton = new JButton("Stop");
        stopButton.setMinimumSize(new Dimension(50, 30));
        stopButton.setPreferredSize(new Dimension(50, 30));
        stopButton.addActionListener(actionEvent -> {
            Logger.Log("Stop button pressed");
            Logger.Log("Opening the confirmation dialog");
            if (onPress != null) {
                onPress.onStopPressed();
            } else Logger.Log("Failed to handle stop button pressed! The handler is null!", true);
        });

        progressText = new JLabel("Starting...", SwingConstants.CENTER);
        progressText.setBorder(BorderFactory.createEmptyBorder());
        attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY, Font.DIALOG);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        attributes.put(TextAttribute.SIZE, 18);
        progressText.setFont(new Font(attributes));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Starting...");
        progressBar.setValue(0);
        progressBar.setMinimumSize(new Dimension(500, 20));
        progressBar.setPreferredSize(new Dimension(500, 20));

        progressPanel = new JPanel();
        progressPanel.setLayout(new FlowLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder());

        frame = new JFrame(Configs.appName);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(Configs.windowX, Configs.windowY);
        frame.setIconImage(logoImg);
        frame.setResizable(false);
    }

    public void setOnPressListener(OnPressListener onPress) {
        this.onPress = onPress;
    }

    private String fixSentence(String sentence) {
        return fixSentence(sentence, false);
    }

    private String fixSentence(String sentence, boolean shorten) {
        String newSentence = Processor.strip(sentence);
        newSentence = newSentence.substring(2, newSentence.length() - 2);
        if (shorten) {
            if (newSentence.contains(".")) {
                newSentence = newSentence.substring(0, newSentence.indexOf("."));
            }
        }
        return newSentence;
    }

    private int[] sentenceBroker(String firstText, String secondText,
                                 String firstSentence, String secondSentence,
                                 Essay firstEssay, Essay secondEssay) {
        firstSentence = fixSentence(firstSentence);
        secondSentence = fixSentence(secondSentence);

        int start = firstText.indexOf(firstSentence);
        int startSecond = secondText.indexOf(secondSentence);
        if (start == -1) firstSentence = fixSentence(firstSentence, true);
        if (startSecond == -1) secondSentence = fixSentence(secondSentence, true);

        int subtraction = 0;
        while (start == -1) {
            try {
                start = firstText.indexOf(firstSentence.substring(subtraction),
                        (firstSentence.length() - 1) - subtraction);
                subtraction++;
            } catch (Throwable ignored) {
                break;
            }
        }

        subtraction = 0;
        while (startSecond == -1) {
            try {
                startSecond = secondText.indexOf(secondSentence.substring(subtraction),
                        (secondSentence.length() - 1) - subtraction);
                subtraction++;
            } catch (Throwable ignored) {
                break;
            }
        }

        int end = (start + firstSentence.length()) + 2;
        int endSecond = (startSecond + secondSentence.length()) + 2;
        if (end > firstText.length()) end = firstText.length();
        if (endSecond > secondText.length()) endSecond = secondText.length();
        if (start == -1 || startSecond == -1) {
            Logger.Log("Couldn't find sentence " + firstSentence + " in " + firstEssay.getName());
            Logger.Log("Or sentence " + secondSentence + " in " + secondEssay.getName());
            return new int[]{0, 0, 0, 0};
        }
        start -= (start >= 2) ? 2 : 0;
        startSecond -= (start >= 2) ? 2 : 0;

        return new int[]{start, startSecond, end, endSecond};
    }

    private String insertNotification(int id, int percentage) {
        return "(((Match: " + id + " Percentage: " + percentage + ")))";
    }

    private BufferedImage renderComponent(Component component) {
        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
                BufferedImage.TYPE_INT_BGR);
        component.paint(image.getGraphics());
        return image;
    }

    public void setReportsLayout(final EssayGroup essayGroup, final List<Flags> essayFlags) {
        //Sort the flagged essays to be highest counts first
        essayFlags.sort((flags, flags2) -> flags2.getCounts() - flags.getCounts());

        final Object[] columnNames = {"Document", "Document2", "Counts", "Highest %"};
        final Object[][] tableData = new Object[essayFlags.size()][columnNames.length];

        for (int ind = 0; ind < essayFlags.size(); ind++) {
            Flags flags = essayFlags.get(ind);
            Logger.Log("Found flags " + flags.getCounts());
            List<Pair<Double, Pair<String, String>>> matches = flags.getMatches();
            Essay firstEssay = essayGroup.getById(flags.getFirstId());
            Essay secondEssay = essayGroup.getById(flags.getSecondId());

            String firstName = firstEssay.getName();
            String secondName = secondEssay.getName();
            int counts = flags.getCounts();

            Logger.Log("Essay one: " + firstName + " two: " + secondName);

            double highestRatio = 0.0;
            for (Pair<Double, Pair<String, String>> match : matches) {
                double ratio = match.getLeft();
                Logger.Log("Ratio: " + ratio + " One: " + match.getRight().getLeft() + " Two: " + match.getRight().getRight());
                if (ratio > highestRatio) highestRatio = ratio;
            }

            int highestPercentage = (int) Math.round(100.0 * highestRatio);

            tableData[ind][0] = firstName;
            tableData[ind][1] = secondName;
            tableData[ind][2] = counts;
            tableData[ind][3] = highestPercentage;
        }

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            frame.dispose();

            JPanel overviewPanel = new JPanel();
            overviewPanel.setLayout(new BoxLayout(overviewPanel, BoxLayout.Y_AXIS));

            JPanel reportPanel = new JPanel();
            reportPanel.setLayout(new GridLayout(0, 2));

            JPanel documentsPanel = new JPanel();
            documentsPanel.setLayout(new GridLayout(2, 1, 10, 10));

            JPanel leftDocumentPanel = new JPanel();
            leftDocumentPanel.setLayout(new FlowLayout());
            JTextArea leftDocument = new JTextArea(15, 40);
            leftDocument.setText("Select an essay by clicking on the table");
            leftDocument.setEditable(false);
            leftDocument.setLineWrap(true);
            leftDocument.setTabSize(4);
            leftDocument.setWrapStyleWord(true);
            leftDocument.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JScrollPane leftDocumentScroll = new JScrollPane(leftDocument);
            leftDocumentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            leftDocumentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JLabel leftDocumentLabel = new JLabel("None Selected");

            leftDocumentPanel.add(leftDocumentLabel);
            leftDocumentPanel.add(leftDocumentScroll);

            JPanel rightDocumentPanel = new JPanel();
            JTextArea rightDocument = new JTextArea(15, 40);
            rightDocument.setText("Select an essay by clicking on the table");
            rightDocument.setEditable(false);
            rightDocument.setLineWrap(true);
            rightDocument.setTabSize(4);
            rightDocument.setWrapStyleWord(true);
            rightDocument.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            JScrollPane rightDocumentScroll = new JScrollPane(rightDocument);
            rightDocumentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            rightDocumentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JLabel rightDocumentLabel = new JLabel("None Selected");

            rightDocumentPanel.add(rightDocumentLabel);
            rightDocumentPanel.add(rightDocumentScroll);

            documentsPanel.add(leftDocumentPanel);
            documentsPanel.add(rightDocumentPanel);
            documentsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            JTable table = new JTable(new DefaultTableModel(tableData, columnNames) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            table.setRowMargin(5);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    super.mouseClicked(mouseEvent);
                    int row = table.getSelectedRow();
                    Logger.Log("The user selected row " + row);
                    try {
                        Flags flags = essayFlags.get(row);
                        Essay firstEssay = essayGroup.getById(flags.getFirstId());
                        Essay secondEssay = essayGroup.getById(flags.getSecondId());

                        leftDocumentLabel.setText(firstEssay.getName());
                        rightDocumentLabel.setText(firstEssay.getName());

                        String firstText = firstEssay.getRawData();
                        String secondText = secondEssay.getRawData();

                        List<Pair<Double, Pair<String, String>>> matches = flags.getMatches();

                        StringBuilder firstBuilder = new StringBuilder(firstText);
                        StringBuilder secondBuilder = new StringBuilder(secondText);

                        int matchId = 1;
                        String percentages[] = new String[matches.size()];

                        for (Pair<Double, Pair<String, String>> match : matches) {
                            double ratio = match.getLeft();
                            String firstSentence = match.getRight().getLeft();
                            String secondSentence = match.getRight().getRight();

                            int brokenSentences[] = sentenceBroker(firstText, secondText, firstSentence, secondSentence,
                                    firstEssay, secondEssay);

                            brokenSentences[0] -= (brokenSentences[0] >= 4) ? 4 : 0;
                            brokenSentences[1] -= (brokenSentences[1] >= 4) ? 4 : 0;

                            int percentage = ((int) Math.round(ratio * 100.0));
                            String notification = insertNotification(matchId, percentage);

                            firstBuilder.insert(brokenSentences[0], notification);
                            secondBuilder.insert(brokenSentences[1], notification);
                            percentages[matchId - 1] = notification;
                            matchId++;
                        }

                        firstText = firstBuilder.toString();
                        secondText = secondBuilder.toString();
                        leftDocument.setText(firstText);
                        rightDocument.setText(secondText);

                        Highlighter leftHighlighter = leftDocument.getHighlighter();
                        Highlighter rightHighligher = rightDocument.getHighlighter();

                        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                        Highlighter.HighlightPainter notificationPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.green);

                        for (Pair<Double, Pair<String, String>> match : matches) {
                            String firstSentence = match.getRight().getLeft();
                            String secondSentence = match.getRight().getRight();

                            int brokenSentences[] = sentenceBroker(firstText, secondText, firstSentence, secondSentence,
                                    firstEssay, secondEssay);


                            leftHighlighter.addHighlight(brokenSentences[0], brokenSentences[2], painter);
                            rightHighligher.addHighlight(brokenSentences[1], brokenSentences[3], painter);

                        }

                        for (String percentage : percentages) {
                            int startLeftNotification = firstText.indexOf(percentage);
                            int endLeftNotification = startLeftNotification + percentage.length();

                            int startRightNotification = secondText.indexOf(percentage);
                            int endRightNotification = startRightNotification + percentage.length();

                            if (startLeftNotification != -1) {
                                leftHighlighter.addHighlight(startLeftNotification, endLeftNotification, notificationPainter);
                            }

                            if (startRightNotification != -1) {
                                rightHighligher.addHighlight(startRightNotification, endRightNotification, notificationPainter);
                            }
                        }

                        leftDocument.repaint();
                        rightDocument.repaint();
                    } catch (Throwable err) {
                        err.printStackTrace();
                        Logger.Log("Failed displaying essay");
                    }
                }
            });
            JScrollPane scrollPane = new JScrollPane(table);
            reportPanel.add(scrollPane);
            reportPanel.add(documentsPanel);

            JPanel footerPanel = new JPanel();
            footerPanel.setLayout(new FlowLayout());


            JButton saveListButton = new JButton("Save Plagiarized List");
            saveListButton.addActionListener(actionEvent -> {
                Logger.Log("Save button pressed");
                BufferedImage renderedTable = renderComponent(reportPanel);
                String date = Logger.getFileDate();
                String imageName = Configs.appName + "-" + date + ".png";
                try {
                    ImageIO.write(renderedTable, "png", new File(imageName));
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.Log("Failed saving " + imageName, true);
                    Dialogs.ErrorDialog(Configs.appName + " | Failed saving", "Failed saving " + imageName);
                }

                String textReportName = Configs.appName + "FullReport" + "-" + date + ".txt";
                try {
                    File reportFile = new File(textReportName);
                    PrintWriter pw = new PrintWriter(new FileOutputStream(reportFile));
                    pw.println(Configs.appName + " Full Report by Pseudonymous");
                    pw.println("Plagiarized counts: " + essayFlags.size());
                    pw.println("Image report: " + imageName);
                    pw.println("Date: " + date);
                    pw.println("-----------------------------------------------------------\n\n");

                    int reportNum = 1;
                    for (Flags flags : essayFlags) {
                        Essay firstEssay = essayGroup.getById(flags.getFirstId());
                        Essay secondEssay = essayGroup.getById(flags.getSecondId());

                        pw.println("Report #" + reportNum + ": " + firstEssay.getName()
                                + " (vs) " + secondEssay.getName() + "\n");

                        int matchNum = 1;
                        for (Pair<Double, Pair<String, String>> match : flags.getMatches()) {
                            pw.println("Match #" + matchNum + ": " + ((int) Math.round(match.getLeft() * 100)) + "%");
                            pw.println("    " + match.getRight().getLeft());
                            pw.println("    " + match.getRight().getRight() + "\n");
                            matchNum++;
                        }

                        pw.println("-----------------------------------------------------------\n\n");

                        reportNum++;
                    }

                    pw.close();
                } catch (Throwable err) {
                    err.printStackTrace();
                    Logger.Log("Failed writing sentence report for " + textReportName);
                    Dialogs.ErrorDialog(Configs.appName + " | Failed saving", "Failed saving report" + textReportName);
                }
            });

            JButton exitButton = new JButton("Exit " + Configs.appName);
            exitButton.addActionListener(actionEvent -> {
                Logger.Log("Exit button pressed");
                Logger.Log("Opening confirmation dialog");
                if (Dialogs.ConfirmDialog(Configs.appName + " | Exit application", "Are you sure you want to exit " + Configs.appName)) {
                    Logger.Log("Exiting " + Configs.appName + "...");
                    System.exit(0);
                }
            });

            footerPanel.add(saveListButton);
            footerPanel.add(exitButton);

            overviewPanel.add(reportPanel);
            overviewPanel.add(footerPanel);

            frame = new JFrame(Configs.appName);
            frame.setContentPane(overviewPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(logoImg);
            frame.setResizable(true);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public void openOptionsWindow() {

        DecimalFormat df = new DecimalFormat("#.###");
        JLabel sensitivityLabel = new JLabel(df.format(Configs.ratioMin), SwingConstants.CENTER);
        DoubleJSlider sensitivitySlider = new DoubleJSlider(0, 100, 0, 100);
        sensitivitySlider.setMinimumSize(new Dimension(300, 50));
        sensitivitySlider.setPreferredSize(new Dimension(300, 50));
        sensitivitySlider.setScaledValue(Configs.ratioMin);
        sensitivitySlider.setPaintLabels(false);
        sensitivitySlider.addChangeListener(changeEvent -> sensitivityLabel.setText(df.format(sensitivitySlider.getScaledValue())));

        JLabel parentSensitivityLabel = new JLabel(df.format(Configs.ratioParentMin), SwingConstants.CENTER);
        DoubleJSlider parentSensitivitySlider = new DoubleJSlider(0, 100, 0, 100);
        parentSensitivitySlider.setMinimumSize(new Dimension(300, 50));
        parentSensitivitySlider.setPreferredSize(new Dimension(300, 50));
        parentSensitivitySlider.setScaledValue(Configs.ratioParentMin);
        parentSensitivitySlider.setPaintLabels(false);
        parentSensitivitySlider.addChangeListener(changeEvent -> parentSensitivityLabel.setText(df.format(parentSensitivitySlider.getScaledValue())));

        JLabel wordLabel = new JLabel(String.valueOf(Configs.sentenceMinWords), SwingConstants.CENTER);
        JSlider wordSlider = new JSlider(0, 50, 0);
        wordSlider.setMinimumSize(new Dimension(300, 50));
        wordSlider.setPreferredSize(new Dimension(300, 50));
        wordSlider.setValue(Configs.sentenceMinWords);
        wordSlider.addChangeListener(changeEvent -> wordLabel.setText(String.valueOf(wordSlider.getValue())));

        JLabel minCountsLabel = new JLabel(String.valueOf(Configs.minCounts), SwingConstants.CENTER);
        JSlider minCountsSlider = new JSlider(0, 50, 0);
        minCountsSlider.setMinimumSize(new Dimension(300, 50));
        minCountsSlider.setPreferredSize(new Dimension(300, 50));
        minCountsSlider.setValue(Configs.minCounts);
        minCountsSlider.addChangeListener(changeEvent -> minCountsLabel.setText(String.valueOf(minCountsSlider.getValue())));

        JLabel minCharsLabel = new JLabel(String.valueOf(Configs.sentenceMinSize), SwingConstants.CENTER);
        JSlider minCharsSlider = new JSlider(0, 50, 0);
        minCharsSlider.setMinimumSize(new Dimension(300, 50));
        minCharsSlider.setPreferredSize(new Dimension(300, 50));
        minCharsSlider.setValue(Configs.sentenceMinSize);
        minCharsSlider.addChangeListener(changeEvent -> minCharsLabel.setText(String.valueOf(minCharsSlider.getValue())));

        JButton saveButton = new JButton("Save");
        saveButton.setMinimumSize(new Dimension(200, 50));
        saveButton.setPreferredSize(new Dimension(200, 50));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.add(new Label("Minimum Sentence Sensitivity", SwingConstants.CENTER), Component.CENTER_ALIGNMENT);
        optionsPanel.add(sensitivityLabel, Component.CENTER_ALIGNMENT);
        optionsPanel.add(sensitivitySlider);
        optionsPanel.add(new Label("Minimum Parent Sentence Sensitivity"));
        optionsPanel.add(parentSensitivityLabel);
        optionsPanel.add(parentSensitivitySlider);
        optionsPanel.add(new Label("Minimum Word Count Per Sentence", SwingConstants.CENTER));
        optionsPanel.add(wordLabel);
        optionsPanel.add(wordSlider);
        optionsPanel.add(new Label("Minimum Character Count Per Sentence", SwingConstants.CENTER));
        optionsPanel.add(minCharsLabel);
        optionsPanel.add(minCharsSlider);
        optionsPanel.add(saveButton, Component.CENTER_ALIGNMENT);

        JFrame optionsFrame = new JFrame();
        optionsFrame.setContentPane(optionsPanel);
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setIconImage(logoImg);
        optionsFrame.setResizable(true);
        optionsFrame.pack();
        optionsFrame.setVisible(true);

        saveButton.addActionListener(actionEvent -> {
            Logger.Log("Save button pressed");
            Configs.sentenceMinWords = wordSlider.getValue();
            Configs.ratioMin = sensitivitySlider.getScaledValue();
            Configs.ratioParentMin = parentSensitivitySlider.getScaledValue();
            Configs.minCounts = minCountsSlider.getValue();
            Configs.saveConfigurations();
            optionsFrame.dispose();
        });
    }

    public void setProgressLayout() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            frame.dispose();

            JPanel textBarPanel = new JPanel();
            textBarPanel.setLayout(new GridLayout(2, 1, 0, 0));
            JPanel textTopBarPanel = new JPanel();
            JPanel textBottomBarPanel = new JPanel();

            textTopBarPanel.setLayout(new BorderLayout());
            textTopBarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textTopBarPanel.add(progressText, BorderLayout.SOUTH);

            textBottomBarPanel.setLayout(new BorderLayout());
            textBottomBarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textBottomBarPanel.add(progressBar, BorderLayout.NORTH);

            textBarPanel.add(textTopBarPanel);
            textBarPanel.add(textBottomBarPanel);

            progressPanel.add(leftPanel);
            progressPanel.add(textBarPanel);
            progressPanel.add(stopButton);


            frame = new JFrame(Configs.appName);
            frame.setContentPane(progressPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent windowEvent) {

                }

                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    Logger.Log("Closed button pressed");
                    Logger.Log("Opening the confirmation dialog");
                    if (onPress != null) {
                        onPress.onStopPressed();
                    } else Logger.Log("Failed to handle stop button pressed! The handler is null!", true);
                }

                @Override
                public void windowClosed(WindowEvent windowEvent) {

                }

                @Override
                public void windowIconified(WindowEvent windowEvent) {

                }

                @Override
                public void windowDeiconified(WindowEvent windowEvent) {

                }

                @Override
                public void windowActivated(WindowEvent windowEvent) {

                }

                @Override
                public void windowDeactivated(WindowEvent windowEvent) {

                }
            });
            frame.setLocationRelativeTo(null);
            frame.setIconImage(logoImg);
            frame.setResizable(true);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public void setProgress(int progress) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            progressBar.setString(progress + "%");
        });
    }

    public void setProgressBarText(String text) {
        SwingUtilities.invokeLater(() -> progressBar.setString(text));
    }

    public void setProgressText(String text) {
        SwingUtilities.invokeLater(() -> progressText.setText(text));
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

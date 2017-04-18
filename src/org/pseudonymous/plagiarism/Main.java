package org.pseudonymous.plagiarism;

import org.pseudonymous.plagiarism.components.Dialogs;
import org.pseudonymous.plagiarism.config.Configs;
import org.pseudonymous.plagiarism.config.EssayGroup;
import org.pseudonymous.plagiarism.config.Flags;
import org.pseudonymous.plagiarism.config.Logger;
import org.pseudonymous.plagiarism.gui.GUI;
import org.pseudonymous.plagiarism.gui.Resources;
import org.pseudonymous.plagiarism.processing.Processor;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by pseudonymous
 */
public class Main implements GUI.OnPressListener {
    public static final String testPath = "/home/smerkous/Documents/testdocuments/";
    public static final String testParentPath = "/home/smerkous/Documents/testparentdocuments/";
    public static Main main;
    public static GUI gui;
    private static volatile boolean processedParents = false;

    private Main() {
    }

    public static void main(String[] args) {
        Logger.init();
        Resources.init();

        try {
            Configs.init();
        } catch (IOException err) {
            err.printStackTrace();
            Dialogs.ErrorDialog(Configs.appName + " | Failed loading configurations",
                    "Couldn't load or create the necessary configuration files");
        }

        gui = new GUI();
        gui.setOnPressListener(Main.getInstance());
        gui.start();
    }

    private static void processEssays(String folder, String parentFolder) {
        gui.setProgressText("Please wait... Parsing the documents content");
        gui.setProgressBarText("Parsing documents");
        EssayGroup essayGroup = Processor.process(folder, parentFolder); //Text preprocessing

        final DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        Thread progressThread = new Thread(() -> {
            while (true) {
                double progress = essayGroup.getProgress();
                String roundedProgress = df.format(progress);
                Logger.Log("Progress: " + roundedProgress + "/100");
                if (processedParents) {
                    long processing = essayGroup.getProcessing();
                    long processed = essayGroup.getProcessed();

                    gui.setProgressText("Processing document " + processed + " out of " + processing);
                }
                gui.setProgress((int) Math.round(progress));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        progressThread.setDaemon(true);
        progressThread.start();

        gui.setProgressText("Please wait... Processing the parent documents");
        gui.setProgressBarText("Processing parent documents");
        essayGroup.computeParents(); //Calculate parent essays
        processedParents = true;
        essayGroup.computeChildren(); //The hardcore processing

        List<Flags> essayFlags = essayGroup.getChildren();

        progressThread.interrupt();

        gui.setProgressBarText("Done!");
        gui.setProgressText("Opening results!");
        processedParents = false;
        gui.setReportsLayout(essayGroup, essayFlags);
    }

    public static synchronized Main getInstance() {
        if (main == null) main = new Main();
        return main;
    }

    @Override
    public void onStartPressed(String folder, String parentFolder) {
        gui.setProgressLayout();
        Thread startThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException err) {
                Logger.Log("Starting thread interrupted!");
            }
            processEssays(folder, parentFolder);
        });
        startThread.setDaemon(true);
        startThread.start();
    }

    @Override
    public void onOptionsPressed() {
        gui.openOptionsWindow();
    }

    @Override
    public void onStopPressed() {
        if (Dialogs.ConfirmDialog(Configs.appName + " | Exit scan", "Are you sure you want to exit?")) {
            Logger.Log("Exiting " + Configs.appName);
            System.exit(0);
        }
    }
}

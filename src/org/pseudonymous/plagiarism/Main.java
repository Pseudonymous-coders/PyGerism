package org.pseudonymous.plagiarism;

import org.apache.commons.lang3.tuple.Pair;
import org.pseudonymous.plagiarism.config.Essay;
import org.pseudonymous.plagiarism.config.EssayGroup;
import org.pseudonymous.plagiarism.config.Flags;
import org.pseudonymous.plagiarism.config.Logger;
import org.pseudonymous.plagiarism.gui.GUI;
import org.pseudonymous.plagiarism.gui.Resources;
import org.pseudonymous.plagiarism.processing.Processor;

import java.util.List;

/**
 * Created by pseudonymous
 */
public class Main {
    public static final String testPath = "/home/smerkous/Documents/testdocuments/";
    public static final String testParentPath = "/home/smerkous/Documents/testparentdocuments/";
    private static GUI gui;

    public static void main(String[] args) {
        Logger.init();
        Resources.init();

        gui = new GUI();
        gui.setOnPressListener(Main::processEssays);
        gui.start();
    }

    private static void processEssays(String folder) {
        EssayGroup essayGroup = Processor.process(testPath, testParentPath); //Text preprocessing

        Thread progressThread = new Thread(() -> {
            while (true) {
                Logger.Log("Progress: " + essayGroup.getProgress() + "/100");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        progressThread.setDaemon(true);
        progressThread.start();

        essayGroup.computeParents(); //Calculate parent essays
        essayGroup.computeChildren(); //The hardcore processing

        List<Flags> essayFlags = essayGroup.getChildren();

        //Sort the flagged essays to be highest counts first
        essayFlags.sort((flags, flags2) -> flags2.getCounts() - flags.getCounts());

        for (Flags flags : essayFlags) {
            Logger.Log("Found flags " + flags.getCounts());
            List<Pair<Double, Pair<String, String>>> matches = flags.getMatches();
            Essay firstEssay = essayGroup.getById(flags.getFirstId());
            Essay secondEssay = essayGroup.getById(flags.getSecondId());

            Logger.Log("Essay one: " + firstEssay.getName() + " two: " + secondEssay.getName());

            for (Pair<Double, Pair<String, String>> match : matches) {
                Logger.Log("Ratio: " + match.getLeft() + " One: " + match.getRight().getLeft() + " Two: " + match.getRight().getRight());
            }
        }

        progressThread.interrupt();
    }
}

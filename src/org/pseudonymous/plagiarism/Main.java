package org.pseudonymous.plagiarism;

import org.apache.commons.lang3.tuple.Pair;
import org.pseudonymous.plagiarism.config.EssayGroup;
import org.pseudonymous.plagiarism.config.Flags;
import org.pseudonymous.plagiarism.config.Logger;
import org.pseudonymous.plagiarism.processing.Processor;

import java.util.List;

/**
 * Created by pseudonymous
 */
public class Main {
    public static final String testPath = "/home/smerkous/Documents/testdocuments/";

    public static void main(String[] args) {
        Logger.init();

        EssayGroup essayGroup = Processor.process(testPath); //Text preprocessing

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Logger.Log("Progress: " + essayGroup.getProgress() + "/100");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        essayGroup.computeChildren(); //The hardcore processing

        List<Flags> essayFlags = essayGroup.getChildren();

        //Sort the flagged essays to be highest counts first
        essayFlags.sort((flags, flags2) -> flags2.getCounts() - flags.getCounts());

        for (Flags flags : essayFlags) {
            Logger.Log("Found flags " + flags.getCounts());
            List<Pair<Double, Pair<String, String>>> matches = flags.getMatches();

            for (Pair<Double, Pair<String, String>> match : matches) {
                Logger.Log("Ratio: " + match.getLeft() + " One: " + match.getRight().getLeft() + " Two: " + match.getRight().getRight());
            }
        }
    }
}

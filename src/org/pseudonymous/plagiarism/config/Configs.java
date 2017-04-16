package org.pseudonymous.plagiarism.config;

/**
 * Created by pseudonymous
 */
public class Configs {
    /*
     * General/Window configurations
     */
    public static final String appName = "PlagiarismChecker";
    public static final int
            windowX = 1280,
            windowY = 720;
    public static final String logLocation = "logs/";
    public static final String logFile = "plagiarism-checker-log.txt";
    public static final String iconName = "logo";

    /*
     * Processing configurations
     */
    public static int sentenceMinSize = 10; //Minimum character count for a sentence
    public static int sentenceMinWords = 5; //Minimum word count for a sentence
    public static int fixedPoolSize = 50; //Allow for 10 threads to run at once
    public static double ratioMin = 0.6; //80 percent match for each sentence
    public static double ratioParentMin = 0.95; //90 percent match for each parent sentence
    public static int minCounts = 1; //Minimum amount of flags to be considered plagiarised
}

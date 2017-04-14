package org.pseudonymous.plagiarism.config;

/**
 * Created by pseudonymous
 */
public class Configs {
    public static int sentenceMinSize = 10; //Minimum character count for a sentence
    public static int sentenceMinWords = 5; //Minimum word count for a sentence
    public static int fixedPoolSize = 50; //Allow for 10 threads to run at once
    public static double ratioMin = 0.8; //80 percent match
    public static int minCounts = 1; //Minimum amount of flags to be considered plagiarised
}

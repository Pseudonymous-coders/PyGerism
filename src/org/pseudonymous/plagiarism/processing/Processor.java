package org.pseudonymous.plagiarism.processing;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.commons.lang3.tuple.Pair;
import org.pseudonymous.plagiarism.config.Essay;
import org.pseudonymous.plagiarism.config.EssayGroup;
import org.pseudonymous.plagiarism.config.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.pseudonymous.plagiarism.Main.gui;

/**
 * Created by pseudonymous
 */
public class Processor {
    public static final NormalizedLevenshtein comparison = new NormalizedLevenshtein();

    /**
     * String similarity ratio calculator
     *
     * @param one First string to compare
     * @param two Second string to compare
     * @return A ratio of 0 to 1 of how close the strings are (0 not at all 1 is the same)
     */
    public static double ratioCalculator(String one, String two) {
        return 1.0f - comparison.distance(one, two);
    }

    /**
     * A list chunking utility
     *
     * @param collection The input list to be chunked
     * @param size       The amount of chunked lists to return
     * @param <T>        The type of sublist you would like to be returned
     * @return A list of sublists for further processing
     */
    public static <T> List<List<T>> partitionList(List<T> collection, int size) {
        List<List<T>> chunked = new ArrayList<>();

        int totalSize = collection.size();

        T lastComponent = (collection.get(totalSize - 1));
        boolean oddArray = false;

        if (totalSize % size != 0) {
            Logger.Log("Partitioning will add one to the end");
            totalSize = (totalSize - 1) / size;
            oddArray = true;
        } else {
            totalSize = totalSize / size;
        }

        Logger.Log("Splitting list into " + totalSize + " sized chunks");

        int transformedSize = collection.size() - ((oddArray) ? 1 : 0);

        for (int ind = 0; ind < transformedSize; ) {
            int nextInc = Math.min(collection.size() - ind, totalSize);
            List<T> chunk = collection.subList(ind, ind + nextInc);
            chunked.add(chunk);
            ind = ind + nextInc;
        }

        if (oddArray) {
            try {
                if (chunked.size() > 0) {
                    List<T> lastChunk = new ArrayList<>();
                    lastChunk.addAll(chunked.get(chunked.size() - 1));
                    lastChunk.add(lastComponent);
                    chunked.set(chunked.size() - 1, lastChunk);
                }
            } catch (Throwable error) {
                error.printStackTrace();
                Logger.Log("Failed to add last component to odd array", true);
            }
        }

        return chunked;
    }

    /**
     * Common string strip function
     *
     * @param s The string to strip spaces from
     * @return The stripped down string
     */
    public static String strip(String s) {
        if (!s.endsWith(" ")) return s;

        String newString = s;
        int subIndex = s.length() - 1;
        while (newString.endsWith(" ")) {
            newString = s.substring(0, subIndex--);
        }

        return newString;
    }

    /**
     * Remove urls from strings
     *
     * @param commentstr The full string to detect a url in
     * @return The newly parsed string that doesn't have the url
     */
    public static String removeUrls(String commentstr) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            String newGroup = m.group(i)
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "")
                    .replaceAll("\\&", "")
                    .replaceAll("\\?", "");
            commentstr = commentstr.replaceAll(newGroup, "").trim();
            i++;
        }
        return commentstr;
    }

    public static String basename(String file) {
        String basename;
        try {
            basename = file.split("\\.(?=[^\\.]+$)")[0];
        } catch (Exception err) {
            Logger.Log("Failed getting basename of file " + file, true);
            basename = file.substring(0, file.length() - 4);
        }
        return basename;
    }

    public static EssayGroup process(String folder) {
        return process(folder, null);
    }

    public static EssayGroup process(String folder, String parentFolder) {
        EssayGroup essayGroup = new EssayGroup();
        try {
            if (!FileConverter.checkIfFolderExists(folder)) {
                Logger.Log("The path " + folder + " doesn't exist", true);
            } else {
                int uniqueId = 0;
                List<Pair<String, String>> fileList = FileConverter.processFolder(folder);
                for (Pair<String, String> filePair : fileList) {
                    if (filePair == null) {
                        Logger.Log("Not a valid file pair", true);
                        return process(folder, parentFolder); //Rerun the entire process @TODO - FIX THIS WEIRD NULLPOINTER BUG
                    }
                    Logger.Log("Processed file " + filePair.getLeft());
                    Essay essay = new Essay(uniqueId);
                    essay.setName(basename(new File(filePair.getLeft()).getName()));
                    essay.setRawData(filePair.getRight());
                    essay.computeSentences();

                    gui.setProgressBarText("Cleaning document" + essay.getName());

                    essayGroup.addChild(essay);
                    uniqueId++;
                }

                if (parentFolder != null) {
                    if (parentFolder.length() != 0) {
                        Logger.Log("Processing parent folder " + parentFolder);
                        fileList = FileConverter.processFolder(parentFolder);
                        for (Pair<String, String> filePair : fileList) {
                            if (filePair == null) {
                                Logger.Log("Not a valid file pair", true);
                                return process(folder, parentFolder); //Rerun the entire process @TODO - FIX THIS WEIRD NULLPOINTER BUG
                            }
                            Logger.Log("Processed parent file " + filePair.getLeft());
                            Essay essay = new Essay(uniqueId);
                            essay.setName(filePair.getLeft());
                            essay.setRawData(filePair.getRight());
                            essay.computeSentences();

                            essayGroup.addParentEssay(essay);
                            uniqueId++;
                        }
                    }
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
            Logger.Log("Failed processing essays", true);
        }

        return essayGroup;
    }
}

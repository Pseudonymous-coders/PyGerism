package org.pseudonymous.plagiarism.processing;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.commons.lang3.tuple.Pair;
import org.pseudonymous.plagiarism.config.Essay;
import org.pseudonymous.plagiarism.config.EssayGroup;
import org.pseudonymous.plagiarism.config.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public static EssayGroup process(String folder) {
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
                        return process(folder); //Rerun the entire process @TODO - FIX THIS WEIRD NULLPOINTER BUG
                    }
                    Logger.Log("Processed file " + filePair.getLeft());
                    Essay essay = new Essay(uniqueId);
                    essay.setName(filePair.getLeft());
                    essay.setRawData(filePair.getRight());
                    essay.computeSentences();

                    essayGroup.addChild(essay);
                    uniqueId++;
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
            Logger.Log("Failed processing essays", true);
        }

        return essayGroup;
    }
}

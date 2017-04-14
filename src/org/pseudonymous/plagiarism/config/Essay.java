package org.pseudonymous.plagiarism.config;

import org.pseudonymous.plagiarism.processing.Processor;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by pseudonymous
 */

public class Essay {
    private String name;
    private String rawData;
    private List<String> sentences;
    private int uniqueID = 0;
    private boolean computedSentences = false;

    /**
     * Essay constructor
     *
     * @param uniqueID A unique id to set for the essay
     */
    public Essay(int uniqueID) {
        this.name = "";
        this.rawData = "";
        this.sentences = new ArrayList<>();
        this.uniqueID = uniqueID;
    }

    /**
     * Set essay name
     *
     * @param name The essay name string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the essay string data
     *
     * @param rawData The entire essay in one string
     */
    public void setRawData(String rawData) {
        this.rawData = rawData;
        this.computedSentences = false;
    }

    /**
     * Override the compute sentences with a custom list of sentences
     *
     * @param sentences A List of sentences
     */
    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }

    /**
     * Get the essays name
     *
     * @return A string with the currently set essay name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the essay raw data
     *
     * @return A string with all of the essays raw data
     */
    public String getRawData() {
        return this.rawData;
    }

    /**
     * Get all sentences from the essay
     * Hint: Call computeSentences() before calling this function
     *
     * @return A list of sentences
     */
    public List<String> getSentences() {
        return this.sentences;
    }

    /**
     * Get the current essay uniqueId
     *
     * @return The current essay uniqueId
     */
    public int getUniqueID() {
        return this.uniqueID;
    }

    /**
     * Internal method to compute and create a list of sentences based on the set rawData
     */
    public void computeSentences() {
        String rawData = this.getRawData();
        String[] lines = rawData.split("\n");
        StringBuilder fixedLines = new StringBuilder();
        for (String line : lines) {
            if (line.length() < Configs.sentenceMinSize) continue;
            fixedLines.append(line);
        }

        rawData = fixedLines.toString();

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(rawData);

        this.sentences = new ArrayList<>();
        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String sentence = Processor.strip(rawData.substring(start, end));
            if (sentence.length() >= Configs.sentenceMinSize && sentence.split(" ").length > Configs.sentenceMinWords) {
                this.sentences.add(sentence);
            }
        }

        this.computedSentences = true;
    }

    /**
     * The core function for comparing two essays
     *
     * @param essay2 The secondary essay to compare this one two
     * @return A new flags object to represent sentence similarities
     */
    public Flags compareTo(Essay essay2) {
        Flags flags = new Flags(this.uniqueID, essay2.uniqueID);

        //Return blank flags if we are comparing the same essay
        if (this.uniqueID == essay2.uniqueID) return flags;

        //Check to make sure that we've calculated sentences from the raw data
        if (!this.computedSentences) {
            this.computeSentences();
        }

        //Check if the other essay has been computed as well
        if (!essay2.computedSentences) {
            essay2.computeSentences();
        }

        List<String> fSentences = this.getSentences();
        List<String> sSentences = essay2.getSentences();

        //Check for every sentence in the first essay with the second to see the ratio calculator
        for (int fInd = 0; fInd < fSentences.size(); fInd++) {
            final String fSent = fSentences.get(fInd);
            for (int sInd = (fInd + 1); sInd < sSentences.size(); sInd++) {
                final String sSent = sSentences.get(sInd);
                double ratio = Processor.ratioCalculator(fSent, sSent); //Check Processor to change method type
                if (ratio > Configs.ratioMin) {
                    flags.addPair(fSent, sSent, ratio); //Add sentence to flags
                }
            }
        }
        return flags;
    }

    /**
     * Essay uniqueId validity checker
     *
     * @param uniqueID The uniqueId to compare essays with
     * @return A boolean if the uniqueId matches the current object
     */
    public boolean isID(int uniqueID) {
        return this.uniqueID == uniqueID;
    }
}

package org.pseudonymous.plagiarism.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pseudonymous
 */
public class EssayGroup {
    private volatile List<Essay> parentEssays;
    private volatile List<String> parentSentences;
    private volatile List<Essay> essays;
    private volatile List<Flags> allFlags;
    private long processing = 0;
    private volatile long processed = 0;

    /**
     * EssayGroup constructor
     */
    public EssayGroup() {
        this.essays = new ArrayList<>();
        this.allFlags = new ArrayList<>();
        this.parentEssays = new ArrayList<>();
        this.parentSentences = new ArrayList<>();
    }

    /**
     * Add a sample parent essay to remove common sentences
     *
     * @param parentEssay Parent essay to extrapolate sentences
     */
    public void addParentEssay(Essay parentEssay) {
        this.parentEssays.add(parentEssay);
    }

    /**
     * Calculate all of the parent essays and extract sentences for further
     * extrapolation of the children sentences
     */
    public void computeParents() {
        for (Essay parent : this.parentEssays) {
            if (!parent.hasComputedSentences()) {
                parent.computeSentences();
            }
            this.parentSentences.addAll(parent.getSentences());
        }
    }

    /**
     * Add a child to the sub essay group
     *
     * @param child The child essay that has similar flags to the parent
     */
    public void addChild(Essay child) {
        this.essays.add(child);
    }

    /**
     * Compare all essays in group with each other
     * Call this before getChildren()
     */
    public void computeChildren() {
        ExecutorService executor = Executors.newFixedThreadPool(Configs.fixedPoolSize);
        processing = 0;
        for (int i = 0; i < this.essays.size(); i++) {
            for (int d = (i + 1); d < this.essays.size(); d++) {
                processing++;
            }
        }

        Logger.Log("Processing " + processing + " files");

        for (int pInd = 0; pInd < this.essays.size(); pInd++) {
            final Essay parent = this.essays.get(pInd);
            for (int cInd = (pInd + 1); cInd < this.essays.size(); cInd++) {
                final Essay child = this.essays.get(cInd);
                child.setParentSentences(this.parentSentences);
                child.extrapolateSentences();
                Runnable worker = () -> {
                    Flags flags = parent.compareTo(child);
                    if (!flags.isSameId()) { //The essays are the same
                        if (flags.getCounts() >= Configs.minCounts) { //Check to see if the counts are greater than our minimum
                            this.allFlags.add(flags);
                        }
                        processed++;
                    }
                };
                executor.execute(worker);
            }
        }

        executor.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executor.isTerminated()) ;
        Logger.Log("Finished processing all essays in group!");
    }

    /**
     * Get a list of all flags between essays
     *
     * @return A list of flags
     */
    public List<Flags> getChildren() {
        return this.allFlags;
    }

    /**
     * Get the essay from the group based on the uniqueId
     *
     * @param uniqueID The uniqueId value to find within the group
     * @return The essay object with that id or null
     */
    public Essay getById(int uniqueID) {
        for (Essay essay : this.essays) {
            if (essay.isID(uniqueID)) return essay;
        }
        return null;
    }

    /**
     * Get the the total amount of essays processing
     *
     * @return The number of essays processing
     */
    public long getProcessing() {
        return this.processing;
    }

    /**
     * Get the current count of essays processed
     *
     * @return The number of currently processed essays
     */
    public long getProcessed() {
        return this.processed;
    }

    /**
     * Get the current progress of the processing
     *
     * @return The percentage (0 - 100) of the current progress
     */
    public double getProgress() {
        if (this.processing == 0) return 0;
        return 100.0f * ((double) this.processed / (double) this.processing);
    }

}

package org.pseudonymous.plagiarism.config;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pseudonymous
 */
public class Flags {
    private int counts = 0, firstId = 0, secondId = 0;
    private List<Pair<Double, Pair<String, String>>> matched;

    /**
     * Flags constructor
     *
     * @param firstId  The first essay uniqueID
     * @param secondId The second essay uniqueID
     */
    Flags(int firstId, int secondId) {
        this.matched = new ArrayList<>();
        this.firstId = firstId;
        this.secondId = secondId;
    }

    /**
     * Add a flagged sentence pair to the list
     *
     * @param sentenceOne The sentence from essay one
     * @param sentenceTwo The sentence from essay two
     * @param ratio       The ratio between both sentences
     */
    public void addPair(final String sentenceOne, final String sentenceTwo, final double ratio) {
        Pair<Double, Pair<String, String>> flagPair = new Pair<Double, Pair<String, String>>() {
            @Override
            public Double getLeft() {
                return ratio;
            }

            @Override
            public Pair<String, String> getRight() {
                return new Pair<String, String>() {
                    @Override
                    public String getLeft() {
                        return sentenceOne;
                    }

                    @Override
                    public String getRight() {
                        return sentenceTwo;
                    }

                    @Override
                    public String setValue(String s) {
                        return null;
                    }
                };
            }

            @Override
            public Pair<String, String> setValue(Pair<String, String> stringStringPair) {
                return null;
            }
        };
        matched.add(flagPair);
        counts++;
    }

    /**
     * Get total flagged essay count
     *
     * @return An integer containing the total flagged essays
     */
    public int getCounts() {
        return this.counts;
    }

    /**
     * Get the first essay id
     *
     * @return A uniqueId of the first essay
     */
    public int getFirstId() {
        return this.firstId;
    }

    /**
     * Get the secondary essay id
     *
     * @return A uniqueId of the second essay
     */
    public int getSecondId() {
        return this.secondId;
    }

    /**
     * Get the uniqueId's of both essays
     *
     * @return A pair containing the first and second essay ids
     */
    public Pair<Integer, Integer> getIdPairs() {
        final int firstId = this.firstId;
        final int secondId = this.secondId;
        return new Pair<Integer, Integer>() {
            @Override
            public Integer getLeft() {
                return firstId;
            }

            @Override
            public Integer getRight() {
                return secondId;
            }

            @Override
            public Integer setValue(Integer integer) {
                return null;
            }
        };
    }

    /**
     * Get all of the flagged sentences from the comparison
     *
     * @return A List containing a double (ratio), String (first sentence), String (second sentences) pairs
     */
    public List<Pair<Double, Pair<String, String>>> getMatches() {
        return this.matched;
    }

    /**
     * Check if the two essays have the same id
     *
     * @return A boolean if the uniqueId's are the same
     */
    public boolean isSameId() {
        return firstId == secondId;
    }
}

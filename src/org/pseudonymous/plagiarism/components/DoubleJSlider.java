package org.pseudonymous.plagiarism.components;

import javax.swing.*;

/**
 * Created by pseudonymous
 */
public class DoubleJSlider extends JSlider {
    private final int scale;

    public DoubleJSlider(int min, int max, int value, int scale) {
        super(min, max, value);
        this.scale = scale;
    }

    public double getScaledValue() {
        return ((double) super.getValue()) / this.scale;
    }

    public void setScaledValue(double value) {
        this.setValue((int) (value * scale));
    }
}
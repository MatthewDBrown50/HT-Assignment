package com.codenamebear.model;

public class WeightedWord implements Comparable<WeightedWord>{

    private String word;
    private double tfIdfValue;

    public WeightedWord(String word, double tfIdfValue) {
        this.word = word;
        this.tfIdfValue = tfIdfValue;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getTfIdfValue() {
        return tfIdfValue;
    }

    public void setTfIdfValue(double tfIdfValue) {
        this.tfIdfValue = tfIdfValue;
    }

    @Override
    public int compareTo(WeightedWord w) {
        return Double.compare(this.tfIdfValue, w.getTfIdfValue());
    }
}

package com.codenamebear.model;

public class Word implements Comparable<Word>{

    private final String word;
    private int count;
    private double tfIdfValue;

    public Word(String word) {
        this.word = word;
        this.count = 1;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTfIdfValue() {
        return tfIdfValue;
    }

    public void setTfIdfValue(double tfIdfValue) {
        this.tfIdfValue = tfIdfValue;
    }

    @Override
    public int compareTo(Word w) {
        return Double.compare(this.tfIdfValue, w.getTfIdfValue());
    }
}

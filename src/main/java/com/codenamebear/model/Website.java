package com.codenamebear.model;

import java.util.ArrayList;

public class Website {

    private final String url;
    private ArrayList<WordCount> wordCounts;
    private HT weightedWords;
    private int totalWords;

    public Website(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<WordCount> getWordCounts() {
        return wordCounts;
    }

    public void setWordCounts(ArrayList<WordCount> wordCounts) {
        this.wordCounts = wordCounts;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public HT getWeightedWords() {
        return weightedWords;
    }

    public void setWeightedWords(HT weightedWords) {
        this.weightedWords = weightedWords;
    }
}

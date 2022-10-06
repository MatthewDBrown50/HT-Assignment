//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.model;

public class Website {

    private final String url;
    private HT words;
    private int totalWords;

    public Website(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public HT getWords() {
        return words;
    }

    public void setWords(HT words) {
        this.words = words;
    }
}

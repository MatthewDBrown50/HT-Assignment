//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.model;

public class WordCount {

    private final String word;
    private int count;

    public WordCount(String word) {
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
}

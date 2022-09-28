//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.model;

public record WeightedWord(String word, double tfIdfValue) implements Comparable<WeightedWord> {

    @Override
    public int compareTo(WeightedWord w) {
        return Double.compare(this.tfIdfValue, w.tfIdfValue());
    }
}

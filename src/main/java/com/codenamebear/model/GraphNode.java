package com.codenamebear.model;

import java.io.Serializable;
import java.util.ArrayList;

/******************************************
 Created on 11/27/2022 by Matthew D Brown
 *******************************************/

public class GraphNode implements Serializable {

    private final String url;
    private int totalWords;
    private HT values;
    private final ArrayList<GraphNode> neighbors;

    public GraphNode(String url)
    {
        this.url = url;
        this.neighbors = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public HT getValues() {
        return values;
    }

    public void addNeighbor(GraphNode neighbor){
        this.neighbors.add(neighbor);
    }

    public ArrayList<GraphNode> getNeighbors() {
        return neighbors;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public void setValues(HT values) {
        this.values = values;
    }
}

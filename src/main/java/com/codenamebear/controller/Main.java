package com.codenamebear.controller;

import com.codenamebear.model.GraphNode;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

//        new MainFrame(new Controller());


        Graph graph = new Graph();

        Scraper scraper = new Scraper(graph);
        scraper.scrapeContent();

        for(GraphNode node : graph.getGraph()){
            System.out.println(node.getUrl());
        }
    }
}

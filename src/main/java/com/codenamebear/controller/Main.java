package com.codenamebear.controller;

import com.codenamebear.model.GraphNode;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

//        new MainFrame(new Controller());


//        Graph graph = new Graph();
//
//        Scraper scraper = new Scraper(graph);
//        scraper.scrapeContent();
//
//        for(GraphNode node : graph.getGraph()){
//            System.out.println(node.getUrl());
//        }



        Graph graph = new Graph();

        ArrayList<String> shortestPath = graph.getShortestPath("https://en.wikipedia.org/wiki/Capitalism", "https://en.wikipedia.org/wiki/Estonian_Jews");

        for(String url : shortestPath){
            System.out.println(url);
        }

    }
}

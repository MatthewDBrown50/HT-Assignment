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

        GraphNode source = graph.getNode("https://en.wikipedia.org/wiki/Paradigm");

        GraphNode oneAway = source.getNeighbors().get(2);

        GraphNode twoAway = oneAway.getNeighbors().get(2);

        GraphNode destination = twoAway.getNeighbors().get(2);


        System.out.println("");
        System.out.println(source.getUrl());
        System.out.println(oneAway.getUrl());
        System.out.println(twoAway.getUrl());
        System.out.println(destination.getUrl());
        System.out.println("\nPath:");



        ArrayList<String> shortestPath = graph.getShortestPath(source.getUrl(), destination.getUrl());

        for(String url : shortestPath){
            System.out.println(url);
        }

    }
}

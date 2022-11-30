package com.codenamebear.controller;

import com.codenamebear.model.GraphNode;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

//        new MainFrame(new Controller());

        Graph graph = new Graph();
//        Scraper scraper = new Scraper(graph);
//        scraper.scrapeContent();
//
//        for(GraphNode node : graph.getRoots()){
//            System.out.println(node.getUrl());
//        }

        GraphNode n1 = new GraphNode("abc");
        GraphNode n2 = new GraphNode("def");
        GraphNode n3 = new GraphNode("ghi");
        GraphNode n4 = new GraphNode("jkl");
        GraphNode n5 = new GraphNode("mno");
        GraphNode n6 = new GraphNode("pqr");
        GraphNode n7 = new GraphNode("stu");
        GraphNode n8 = new GraphNode("vwx");
        GraphNode n9 = new GraphNode("yz");
        GraphNode n10 = new GraphNode("123");
        GraphNode n11 = new GraphNode("456");
        GraphNode n12 = new GraphNode("789");
        GraphNode n13 = new GraphNode("111");

        ArrayList<GraphNode> roots = new ArrayList<>();
        roots.add(n1);
        roots.add(n2);
        roots.add(n3);

        addNeighbors(n1,n4);
        addNeighbors(n4,n5);
        addNeighbors(n2,n6);
        addNeighbors(n2,n7);
        addNeighbors(n7,n8);
        addNeighbors(n8,n9);
        addNeighbors(n8,n10);
        addNeighbors(n10,n11);
        addNeighbors(n3,n12);
        addNeighbors(n12,n13);

        graph.setRoots(roots);

        GraphNode newNode1 = graph.getNode("abc");
        GraphNode newNode2 = graph.getNode("def");
        GraphNode newNode3 = graph.getNode("ghi");
        GraphNode newNode4 = graph.getNode("jkl");
        GraphNode newNode5 = graph.getNode("mno");
        GraphNode newNode6 = graph.getNode("pqr");
        GraphNode newNode7 = graph.getNode("stu");
        GraphNode newNode8 = graph.getNode("vwx");
        GraphNode newNode9 = graph.getNode("yz");
        GraphNode newNode10 = graph.getNode("123");
        GraphNode newNode11 = graph.getNode("456");
        GraphNode newNode12 = graph.getNode("789");
        GraphNode newNode13 = graph.getNode("111");

        System.out.println(newNode1.getUrl());
        System.out.println(newNode2.getUrl());
        System.out.println(newNode3.getUrl());
        System.out.println(newNode4.getUrl());
        System.out.println(newNode5.getUrl());
        System.out.println(newNode6.getUrl());
        System.out.println(newNode7.getUrl());
        System.out.println(newNode8.getUrl());
        System.out.println(newNode9.getUrl());
        System.out.println(newNode10.getUrl());
        System.out.println(newNode11.getUrl());
        System.out.println(newNode12.getUrl());
        System.out.println(newNode13.getUrl());
    }

    public static void addNeighbors(GraphNode n1, GraphNode n2){
        n1.addNeighbor(n2);
        n2.addNeighbor(n1);
    }
}

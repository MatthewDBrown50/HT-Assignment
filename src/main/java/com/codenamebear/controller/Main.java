package com.codenamebear.controller;

import com.codenamebear.view.MainFrame;

public class Main {

    public static void main(String[] args){

        Graph graph = new Graph();
        new MainFrame(graph, new Scraper(graph));
    }
}

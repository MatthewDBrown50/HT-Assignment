package com.codenamebear.controller;

import com.codenamebear.model.GraphNode;
import com.codenamebear.model.HT;
import com.codenamebear.utility.WebTextProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/******************************************
 Created on 11/29/2022 by Matthew D Brown
 *******************************************/

public class Scraper {

    Graph graph;
    private final ArrayList<String> ignoredWords;
    private final List<String> websites;
    private GraphNode currentNode;

    /** CONSTRUCTOR */
    public Scraper(Graph graph) throws IOException {

        // Establish a list of ignored words
        List<String> ignoredWordsList = Files.readAllLines(Paths.get("src/main/resources/filter.txt"), StandardCharsets.UTF_8);
        this.ignoredWords = (ArrayList<String>) ignoredWordsList;

        this.websites = new ArrayList<>();

        this.graph = graph;
        graph.setRoots(new ArrayList<>());
    }

    /** SCRAPING STARTS HERE */
    public void scrapeContent() throws IOException {

        // Delete existing roots file
        File rootsFile = new File("src/main/resources/roots.ser");
        if(!rootsFile.delete()){
            System.out.println("Failed to delete roots file");
        }

        // Start with an empty roots list
        this.graph.setRoots(new ArrayList<>());

        try {
            // Establish list of URL addresses from the 'seeds.txt' resource
            List<String> urls = Files.readAllLines(Paths.get("src/main/resources/seeds.txt"), StandardCharsets.UTF_8);

            // For the webpage at each URL:
            for(String url : urls) {

                // Add this to the list of websites, create a new root node, and set its word counts
                addWebsite(url);

                // Add the new root node to the list of roots
                this.graph.getRoots().add(this.currentNode);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finish populating the graph
        populateGraph();

        // Add TF-IDF values to the hashtable for each GraphNode
        for(String url : this.websites){

            GraphNode node = graph.getNode(url);

            System.out.println("\nSetting weights for: " + node.getUrl() + " with values: " + node.getValues());

            setTfIdfValues(node, this.websites.size());

        }

        // Serialize this.roots arraylist
        serializeRoots();
    }

    /** Create a new GraphNode with the specified URL
     *  Scrape the data from the webpage at the URL
     *  Assign an HT to the new GraphNode with word counts
     *  Assign the new GraphNode to this.currentNode
     */
    public void addWebsite(String url) throws IOException {

        // Establish a new GraphNode with the specified URL
        GraphNode node = new GraphNode(url);

        // Extract the text content from the webpage at the specified URL
        String content = WebTextProcessor.extractTextFromUrl(url);


        // Get the total number of words, along with the word count for each word in the text content,
        // then assign them to the website object
        WebTextProcessor.setWordCounts(content, node, this.ignoredWords);


        // Add the URL to the list of websites
        this.websites.add(url);

        // Assign the new GraphNode to this.currentNode
        this.currentNode = node;
    }

    private void populateGraph() throws IOException {

        // Establish a HashSet for keeping track of which nodes have already been processed
        Set<String> settled = new HashSet<>();

        // Establish how many more GraphNodes should be added to the graph
        int remainingLinkCount = 80;

        // Establish how many links should be used from each site
        int linksPerSite = 5;

        // Establish an ArrayList to hold the URL of each of the links gathered from all the roots
        ArrayList<String> allNeighbors = new ArrayList<>();

        // For each root:
        for(GraphNode root : this.graph.getRoots()){

            // Add the root to the list of settled nodes
            settled.add(root.getUrl());

            // Call addLinksToGraph() for this root, which selects up to 5 links from the root and assigns them as
            // neighbor GraphNodes to the root. Save the list of the neighbors' URLs to a new ArrayList.
            ArrayList<String> newNeighbors = addLinksToGraph(root, linksPerSite, settled);

            // Add the list of this root's new neighbors to the list of ALL neighbors for the roots
            allNeighbors.addAll(newNeighbors);

        }

        // Reduce the number of remaining GraphNodes to add by the number that have been added so far
        remainingLinkCount -= allNeighbors.size();

        while(remainingLinkCount > 0){

            // Create a copy of the 'allNeighbors' ArrayList so that it can be reset
            ArrayList<String> allNeighborsCopy = new ArrayList<>(allNeighbors);

            // Reset the 'allNeighbors' ArrayList
            allNeighbors = new ArrayList<>();

            // For each neighbor that was in the 'allNeighbors' list:
            for(String url : allNeighborsCopy){

                // TODO: should this really be a new node?
                GraphNode newNode = new GraphNode(url);

                ArrayList<String> newNeighbors = addLinksToGraph(newNode, linksPerSite, settled);

                allNeighbors.addAll(newNeighbors);

            }

            remainingLinkCount -= allNeighbors.size();
        }
    }

    private ArrayList<String> addLinksToGraph(GraphNode node, int linksPerSite,
                                              Set<String> settled) throws IOException {

        ArrayList<String> linkList = new ArrayList<>();

        try
        {
            Document doc = Jsoup.connect(node.getUrl()).get();
            Elements links = doc.select("a[href]");

            for(Element link : links)
                linkList.add(link.attr("abs:href"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        int linksRemaining = linksPerSite;
        ArrayList<String> newNeighbors = new ArrayList<>();

        while(linksRemaining > 0 && !linkList.isEmpty()){

            int index = (int) (Math.random() * linkList.size());
            String url = linkList.get(index);

            if(url.startsWith("https://en.wikipedia.org") && url.contains("/wiki/") && !url.substring(6).contains(":")
                    && !url.contains("#") && !url.contains("%")){

                // Connect to the URL
                Document document = Jsoup.connect(url).get();

                // Store the contents of all <p> elements on the webpage
                Elements links = document.select("p");

                // Combine the contents from the <p> elements into a single string
                StringBuilder sb = new StringBuilder();
                for(Element link : links){
                    sb.append(link.text());
                    sb.append(" ");
                }

                if(sb.length() > 1000){

                    GraphNode existingNode = graph.getNode(url);

                    if(existingNode != null){

                        connectNeighbors(node, existingNode);

                    } else {

                        GraphNode newNode = new GraphNode(this.currentNode.getUrl());

                        addWebsite(url);
                        connectNeighbors(node, newNode);
                        newNeighbors.add(url);
                        linksRemaining--;
                        linkList.remove(index);
                    }

                    linkList.remove(index);
                }
            }
        }

        settled.add(node.getUrl());

        return newNeighbors;
    }

    private void connectNeighbors(GraphNode node1, GraphNode node2){
        node1.addNeighbor(node2);
        node2.addNeighbor(node1);
    }

    private void setTfIdfValues(GraphNode node, int numberOfWebsites){

        // Assign a Hash Table to each node with the words that appear on the page and their weighted values
        HT ht = WebTextProcessor.getWeightedWords(node, numberOfWebsites);
        node.setValues(ht);
    }

    private void serializeRoots(){
        String sitePath = "src/main/resources/roots.ser";
        try{
            FileOutputStream fileOut = new FileOutputStream(sitePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.graph.getRoots());
            out.close();
            fileOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

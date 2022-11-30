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


        // TODO: REMOVE THIS LOOP
        for(String url : this.websites){

            GraphNode node = graph.getNode(url);
            System.out.println("\nURL: " + url);
            System.out.println("Node URL: " + node.getUrl());
            System.out.println("Values: " + node.getValues());
        }



        // Add TF-IDF values to the hashtable for each GraphNode
        for(String url : this.websites){

            GraphNode node = graph.getNode(url);

//            System.out.println("\nSetting weights for: " + node.getUrl() + " with values: " + node.getValues());

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

                // Process links for the neighbor and assign them to the newNeighbors list
                ArrayList<String> newNeighbors = addLinksToGraph(graph.getNode(url), linksPerSite, settled);

                // add the new neighbors to the allNeighbors list
                allNeighbors.addAll(newNeighbors);

            }

            remainingLinkCount -= allNeighbors.size();
        }
    }

    /**
     *
     */
    private ArrayList<String> addLinksToGraph(GraphNode node, int linksPerSite,
                                              Set<String> settled) throws IOException {

        // Establish an ArrayList for keeping track of the links found on the webpage at the node's URL
        ArrayList<String> linkList = new ArrayList<>();

        try
        {
            // Get all the links on the webpage as elements
            Document doc = Jsoup.connect(node.getUrl()).get();
            Elements links = doc.select("a[href]");

            // Transfer the URLs from the elements to the linkList ArrayList
            for(Element link : links)
                linkList.add(link.attr("abs:href"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Establish int variable for keeping track of how many more neighbors may be added from this node's links
        int linksRemaining = linksPerSite;

        // Establish int variable for keeping track of how many existing nodes get connected to this node
        int existingNodesConnected = 0;

        // Establish an ArrayList for keeping track of the URLs for the new neighbors that get added
        ArrayList<String> newNeighbors = new ArrayList<>();

        // While this node has not exceeded its limit for new neighbors, and it has not run out of links:
        while(linksRemaining > 0 && !linkList.isEmpty() && existingNodesConnected < 5){

            // Assign the URL from a random index in the linkList to the url String variable
            int index = (int) (Math.random() * linkList.size());
            String url = linkList.get(index);

            // If the URL is valid for our purposes:
            if(url.startsWith("https://en.wikipedia.org") && url.contains("/wiki/") && !url.substring(6).contains(":")
                    && !url.contains("#") && !url.contains("%")){

                // Extract text from the webpage so that content length can be assessed
                String content = WebTextProcessor.extractTextFromUrl(url);

                // If the content on the page contains a sufficient number of characters:
                if(content.length() > 8000){

                    // See if the node already exists
                    GraphNode existingNode = graph.getNode(url);

                    // If the node already exists:
                    if(existingNode != null){


                        // TODO: REMOVE THIS
                        System.out.println("\n" + url + " found!");
                        System.out.println("Connecting node " + node.getUrl() + " to EXISTING node " + existingNode.getUrl());


                        // Connect node to the existing node and increment existingNodesConnected
                        connectNeighbors(node, existingNode);
                        existingNodesConnected++;

                        // Otherwise:
                    } else {

                        // Create a new node with the URL, stored in this.currentNode, and scrape content for it
                        addWebsite(url);

                        // Connect node with the new node
                        connectNeighbors(node, this.currentNode);

                        // Add the new node's URL to the list of new neighbors
                        newNeighbors.add(url);

                        // Decrement linksRemaining
                        linksRemaining--;
                    }
                }
            }

            // Remove the URL from the list of available links, in order to avoid adding the same link twice
            linkList.remove(index);
        }

        // Mark current node as settled, then return its new neighbors
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

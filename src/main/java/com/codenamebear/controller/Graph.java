package com.codenamebear.controller;

import com.codenamebear.model.GraphNode;
import com.codenamebear.model.HT;
import com.codenamebear.model.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/******************************************
 Created on 11/27/2022 by Matthew D Brown
 *******************************************/

public class Graph {

    private ArrayList<ArrayList<CostNode>> costLists;
    private ArrayList<GraphNode> graph;
    private ArrayList<String> websites;

    // This node is used to track the 'found node' when searching for a node with getNode()
    // Due to the concurrent nature of the getNode() and searchNeighbors methods, making this object local
    //   leads to improper null returns
    private GraphNode foundNode;

    /**
     * CONSTRUCTOR
     */
    @SuppressWarnings("unchecked")
    public Graph() {

        System.out.println("\nLoading graph. Please allow 1 or 2 minutes for this process to complete...");

        // Establish a File with path to serialized graph file location
        String graphFilePath = "src/main/resources/graph.ser";
        File graphFile = new File(graphFilePath);

        // Establish a File with path to serialized websites file location
        String websitesFilePath = "src/main/resources/websites.ser";
        File websitesFile = new File(websitesFilePath);

        // Determine if the files currently exist
        boolean filesExist = graphFile.exists() && websitesFile.exists();

        // If the files exist:
        if(filesExist){

            // Deserialize graph.ser and websites.ser, and assign them to this.graph and this.websites, respectively
            try {
                FileInputStream fileIn = new FileInputStream(graphFilePath);
                ObjectInputStream inputStream = new ObjectInputStream(fileIn);
                this.graph = (ArrayList<GraphNode>) inputStream.readObject();
                inputStream.close();
                fileIn.close();
            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                FileInputStream fileIn = new FileInputStream(websitesFilePath);
                ObjectInputStream inputStream = new ObjectInputStream(fileIn);
                this.websites = (ArrayList<String>) inputStream.readObject();
                inputStream.close();
                fileIn.close();
            } catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("\nGraph loaded");

            // Otherwise, initialize this.graph and this.website and new ArrayLists
        } else {

            System.out.println("\nSerialized graph not found. Starting with empty graph.");

            this.graph = new ArrayList<>();
            this.websites = new ArrayList<>();
        }
    }

    /**
     * INSTANCES OF THIS RECORD ARE USED TO KEEP TRACK OF EDGE COSTS WHEN DETERMINING THE SHORTEST PATH
     */
    record CostNode(String url, double cost) {}

    /**
     * CHECK TO SEE IF ANY OF THE SEEDS ARE THE NODE WE'RE LOOKING FOR.
     * IF NOT, THEN CALL searchNeighbors() FOR EACH NEIGHBOR OF EACH SEED
     */
    public GraphNode getNode(String sourceUrl){

        // Initialize foundNode to null so that it doesn't return the result from a previous search
        this.foundNode = null;

        // Establish a hashset to keep track of which nodes have been checked.
        Set<String> settled = new HashSet<>();

        // For each seed node:
        for(GraphNode seed : this.graph){

            // If the seed is the node we're looking for, return it
            if(seed.getUrl().equals(sourceUrl)){
                return seed;

                // Otherwise:
            } else {

                // Mark the seed as settled
                settled.add(seed.getUrl());

                // For each neighbor of the seed:
                for (GraphNode neighbor : seed.getNeighbors()){

                    // Check the neighbor node, as well as its neighbors
                    GraphNode sourceNode = searchNeighbors(neighbor, sourceUrl, settled);

                    // If a node was returned, and it was the one we're looking for, then assign it to the return
                    if(sourceNode != null){
                        if (sourceNode.getUrl().equals(sourceUrl)){
                            this.foundNode = sourceNode;
                        }
                    }

                }
            }
        }
        return this.foundNode;
    }

    /**
     * DETERMINE IF THE NODE IS THE ONE WE'RE LOOKING FOR
     * IF IT'S NOT, THEN CALL THIS METHOD RECURSIVELY WITH
     *   EACH OF THE NODE'S NEIGHBORS (EXCEPT ONES THAT HAVE BEEN SETTLED)
     */
    private GraphNode searchNeighbors(GraphNode node, String sourceUrl, Set<String> settled){

        // If this node is the one we're searching for then assign it to foundNode
        if(node.getUrl().equals(sourceUrl)){

            this.foundNode = node;

            // Otherwise:
        } else {

            // Mark the node as settled
            settled.add(node.getUrl());

            // For each neighbor that isn't 'settled', call searchNeighbors() with that node
            for(GraphNode neighbor : node.getNeighbors()){

                if(!settled.contains(neighbor.getUrl())){
                    searchNeighbors(neighbor, sourceUrl,settled);
                }
            }
        }

        return this.foundNode;
    }

    /**
     * RUN dijkstra() WITH THE SPECIFIED sourceUrl AND destinationUrl
     */
    public ArrayList<String> getShortestPath(String sourceUrl, String destinationUrl){

        // Initialize a double for tracking the cheapest cost, along
        // with an ArrayList that stores the path with that cost
        double cheapestCost = Integer.MAX_VALUE;
        ArrayList<String> shortestPath = new ArrayList<>();

        // If the source and destination are the same, simply return an ArrayList that only contains the source
        if(sourceUrl.equals(destinationUrl)){
            shortestPath.add(sourceUrl);
            return shortestPath;
        }

        // Populate this.costLists with paths from source to destination
        dijkstra(sourceUrl, destinationUrl);

        // For each path in this.costLists:
        for(ArrayList<CostNode> costList : this.costLists){

            // Initialize a variable for tracking the cost of the path
            double cost = 0;

            // Add the cost of each costNode in the path to the cost variable
            for(CostNode costNode : costList){
                cost += costNode.cost();
            }

            // If this is the cheapest path so far, create a new ArrayList with the
            // URLs of the path and assign it to shortestPath, then update cheapestCost
            if (cost < cheapestCost){
                ArrayList<String> path = new ArrayList<>();

                for(CostNode costNode : costList){
                    path.add(costNode.url());
                }

                shortestPath = path;

                cheapestCost = cost;
            }
        }

        return shortestPath;
    }


    /**
     * ESTABLISH AN ARRAYLIST TO HOLD ARRAYLISTS OF CostNode OBJECTS
     * ESTABLISH A COST ARRAYLIST FOR EACH NEIGHBOR OF THE SOURCE NODE
     * FOR EACH NEIGHBOR, CALL processNeighbors() TO BUILD ALL THE PATHS FROM THAT NODE
     * ADD THE ARRAYLIST FOR EACH PATH THAT FINDS THE DESTINATION NODE TO this.costLists
     */
    public void dijkstra(String sourceUrl, String destinationUrl) {

        // Find the node for the specified URL and assign it to sourceNode
        GraphNode sourceNode = getNode(sourceUrl);

        // Initialize an ArrayList that will hold cost ArrayLists for each path that finds the destination
        costLists = new ArrayList<>();

        // This should always be true
        if(sourceNode != null){

            // For each neighbor of the node:
            for(GraphNode neighbor : sourceNode.getNeighbors()){

                // Establish an ArrayList for storing costs, and add a CostNode for the source node to it
                ArrayList<CostNode> costList = new ArrayList<>();
                costList.add(new CostNode(sourceNode.getUrl(), 0));

                // Establish a HashSet for keeping track of which nodes have already been visited on the path
                //   and add the source node to it
                Set<String> settled = new HashSet<>();
                settled.add(sourceUrl);

                // Begin building the paths
                processNeighbors(neighbor,destinationUrl,settled,costList);
            }

        } else {

            // We will never see this warning if everything is implemented correctly
            System.out.println("Something went wrong: sourceNode was null in Graph.dijkstra()");
        }
    }

    /**
     * START BUILDING A PATH OF COSTNODES FROM EACH UNSETTLED NEIGHBOR OF THE SOURCE NODE
     * MAKE RECURSIVE CALLS TO THIS METHOD UNTIL THE DESTINATION IS FOUND, OR WE RUN OUT OF UNSETTLED NEIGHBORS TO
     *   CONTINUE THE PATH WITH
     */
    private void processNeighbors(GraphNode source, String destinationUrl,
                                  Set<String> settled, ArrayList<CostNode> costList){

        // For each neighbor of the source node:
        for(GraphNode neighbor : source.getNeighbors()){

            // If the neighbor isn't already settled:
            if(!settled.contains(neighbor.getUrl())){

                // Establish a separate costList for the path that continues with this neighbor
                ArrayList<CostNode> newCostList = new ArrayList<>(costList);

                // Mark the neighbor as settled
                settled.add(neighbor.getUrl());

                // Get the cost for traveling from the source node to the neighbor and add it to the newCostList
                double edgeDistance = getCost(source.getValues(), neighbor.getValues());
                newCostList.add(new CostNode(neighbor.getUrl(), edgeDistance));

                // If the neighbor is the node we're looking for, add the newCostList to this.costLists
                if(neighbor.getUrl().equals(destinationUrl)){
                    costLists.add(newCostList);

                    // Otherwise, call this method again, with neighbor as the source node
                }else {
                    processNeighbors(neighbor, destinationUrl, settled, newCostList);
                }
            }
        }
    }

    /**
     * RETURN THE COST VALUE FOR TRAVERSING THE EDGE FROM website1 TO website2
     */
    private double getCost(HT website1, HT website2){

        // Initialize a match value for the sites
        double matchValue = 0;

        // For each word in website1's hashtable:
        for (Word word : website1.getKeys()) {
            String nextWord = word.getWord();

            // If the content of websites.get(i) contains the specified word in the user's content:
            if (website2.contains(nextWord)) {

                // Get the weighted value for the word in the user's document
                double userSiteTfIdf = website2.getWeight(word.getWord());

                // Get the weighted value for the word in the websites.get(i) document
                double websiteTfIdf = website1.getWeight(word.getWord());

                // Multiply the weights to establish a match value, then return it
                matchValue += userSiteTfIdf * websiteTfIdf;
            }
        }

        // Since we're working with distance, we return 1/matchValue, so that better matches give shorter distance
        return (1 / matchValue);
    }

    /**
     * GETTERS AND SETTERS
     */
    public ArrayList<GraphNode> getGraph() {
        return graph;
    }

    public void setGraph(ArrayList<GraphNode> graph) {
        this.graph = graph;
    }

    public ArrayList<String> getWebsites() {
        return websites;
    }

    public void setWebsites(ArrayList<String> websites) {
        this.websites = websites;
    }
}

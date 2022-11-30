package com.codenamebear.controller;

import com.codenamebear.model.GraphNode;
import com.codenamebear.model.HT;
import com.codenamebear.model.Word;
import java.io.IOException;
import java.util.*;

/******************************************
 Created on 11/27/2022 by Matthew D Brown
 *******************************************/

public class Graph {

    private ArrayList<ArrayList<CostNode>> costLists;
    private ArrayList<GraphNode> roots;

    // This node is used to track the 'found node' when searching for a node with getNode()
    // Due to the concurrent nature of the getNode() method, making this object local to the getNode() method
    //   leads to incorrect null returns
    private GraphNode foundNode;

    /**
     * CONSTRUCTOR
     */
    public Graph() throws IOException {
        this.roots = new ArrayList<>();
        this.foundNode = null;
    }

    /**
     * INSTANCES OF THIS RECORD ARE USED TO KEEP TRACK OF EDGE COSTS WHEN DETERMINING THE SHORTEST PATH
     */
    record CostNode(String url, double cost) {}

    /**
     * RUN dijkstra() WITH THE SPECIFIED sourceUrl AND destinationUrl
     */
    public ArrayList<String> getShortestPath(String sourceUrl, String destinationUrl){


        dijkstra(sourceUrl, destinationUrl);

        double shortestCost = Integer.MAX_VALUE;
        ArrayList<String> shortestPath = new ArrayList<>();

        for(ArrayList<CostNode> costList : this.costLists){
            double cost = 0;

            for(CostNode costNode : costList){
                cost += costNode.cost();
            }

            if (cost < shortestCost){
                ArrayList<String> path = new ArrayList<>();

                for(CostNode costNode : costList){
                    path.add(costNode.url());
                }

                shortestCost = cost;
                shortestPath = path;
            }
        }

        return shortestPath;
    }


    /**
     * ESTABLISH AN ARRAYLIST TO HOLD ARRAYLISTS OF CostNode OBJECTS
     * ESTABLISH A COST ARRAYLIST FOR EACH NEIGHBOR OF THE SOURCE NODE
     * FOR EACH NEIGHBOR, CALL processNeighbors() TO BUILD ALL THE PATHS FROM THAT NODE
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

    // TODO: FIGURE OUT WHY getNode() ISN'T WORKING CORRECTLY

    /**
     * CHECK TO SEE IF ANY OF THE ROOTS ARE THE NODE WE'RE LOOKING FOR.
     * IF NOT, THEN CALL searchNeighbors() FOR EACH NEIGHBOR OF EACH ROOT
     */
    public GraphNode getNode(String sourceUrl){

        // Establish a hashset to keep track of which nodes have been checked.
        Set<String> settled = new HashSet<>();

        // For each root node:
        for(GraphNode root : this.roots){

            // If the root is the node we're looking for, return it
            if(root.getUrl().equals(sourceUrl)){
                return root;

                // Otherwise:
            } else {

                // Mark the root as settled
                settled.add(root.getUrl());

                // For each neighbor of the root:
                for (GraphNode neighbor : root.getNeighbors()){

                    // Check the neighbor node, as well as its neighbors
                    GraphNode sourceNode = searchNeighbors(neighbor, sourceUrl, settled);

                    // If a node was returned, and it was the one we're looking for, then return it
                    if(sourceNode != null){
                        if (sourceNode.getUrl().equals(sourceUrl)){
                            return sourceNode;
                        }
                    }

                }
            }
        }
        return null;
    }

    /**
     * DETERMINE IF THE NODE IS THE ONE WE'RE LOOKING FOR
     * IF IT'S NOT, THEN CALL THIS METHOD RECURSIVELY WITH
     *   EACH OF THE NODE'S NEIGHBORS (EXCEPT ONES THAT HAVE BEEN SETTLED)
     */
    private GraphNode searchNeighbors(GraphNode node, String sourceUrl, Set<String> settled){

        // Establish a return node
        this.foundNode = null;

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

    private void processNeighbors(GraphNode source, String destinationUrl,
                                  Set<String> settled, ArrayList<CostNode> costList){

        for(GraphNode neighbor : source.getNeighbors()){

            if(!settled.contains(neighbor.getUrl())){
                double edgeDistance = getCost(source.getValues(), neighbor.getValues());
                settled.add(neighbor.getUrl());
                costList.add(new CostNode(neighbor.getUrl(), edgeDistance));

                if(neighbor.getUrl().equals(destinationUrl)){

                    costLists.add(costList);

                }else {

                    processNeighbors(neighbor, destinationUrl, settled, costList);
                }
            }
        }
    }

    private double getCost(HT website1, HT website2){

        double matchValue = 0;

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

    public ArrayList<GraphNode> getRoots() {
        return roots;
    }

    public void setRoots(ArrayList<GraphNode> roots) {
        this.roots = roots;
    }
}
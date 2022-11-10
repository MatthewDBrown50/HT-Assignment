package com.codenamebear.utility;

import com.codenamebear.model.Website;
import com.codenamebear.model.Word;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/******************************************
 Created on 10/30/2022 by Matthew D Brown
 *******************************************/

public class KMedoidsProducer {

    private final int numberOfMedoids;
    private final List<Website> websites;
    private final ArrayList<ArrayList<Website>> medoids;

    public KMedoidsProducer(List<Website> websites, int numberOfMedoids){
        this.numberOfMedoids = numberOfMedoids;
        this.websites = websites;
        this.medoids = new ArrayList<>();
    }

    public ArrayList<ArrayList<Website>> getMedoids(){

        // From our list of websites, select 7 at random to serve as our initial medoid centers
        initializeMedoids();

        // Place each website in the medoid whose center best matches the website's content
        populateMedoids();

        // Evaluate each medoid, and replace its center (if applicable) with whichever website in the medoid has the
        // best match rate with all the other websites in that same medoid
        improveMedoids();

        return this.medoids;
    }

    // From our list of websites, select 7 at random to serve as our initial medoid centers
    private void initializeMedoids(){

        Random random = new Random();

        for(int i = 0; i < numberOfMedoids; i++){

            int index = random.nextInt(websites.size());

            medoids.add(new ArrayList<>());
            medoids.get(i).add(websites.get(index));

            websites.remove(index);
        }
    }

    // Place each website in the medoid whose center best matches the website's content
    private void populateMedoids(){

        for(Website website : websites){

            double bestMatchValue = 0;
            int placementIndex = 0;
            ArrayList<Word> words = website.getWords().getKeys();

            // For each medoid in our list of medoids:
            for(int i = 0; i < numberOfMedoids; i++){

                // Initialize a value that measures how well content of websites.get(i) matches the medoid's center
                double matchValue = 0;

                // For each word in the website
                for (Word word : words) {
                    String nextWord = word.getWord();

                    // If the content of the medoid center contains the specified word from the website's content:
                    if (this.medoids.get(i).get(0).getWords().contains(nextWord)) {

                        // Get the weighted value for the word in the website's document
                        double siteTfIdf = website.getWords().getWeight(word.getWord());

                        // Get the weighted value for the word in the center's document
                        double centerTfIdf = this.medoids.get(i).get(0).getWords().getWeight(word.getWord());

                        // Multiply the weights to establish a match value, then add it to the total match value for the
                        // website
                        double currentMatchValue = siteTfIdf * centerTfIdf;
                        matchValue += currentMatchValue;
                    }

                }

                // If the content of the website is a better match for the user's content than the previously stored
                // match, then overwrite these values
                if(matchValue >= bestMatchValue){
                    bestMatchValue = matchValue;
                    placementIndex = i;
                }
            }

            // Add the website to the appropriate medoid
            medoids.get(placementIndex).add(website);
        }
    }

    // Evaluate each medoid, and replace its center (if applicable) with whichever website in the medoid has the
    // best match rate with all the other websites in that same medoid
    private void improveMedoids(){

        // For each medoid in our list of medoids:
        for(ArrayList<Website> medoid : this.medoids){

            // Initialize best match value and a best-matching center for the medoid
            double bestMatchValue = 0;
            Website bestMatch = medoid.get(0);

            // For each medoid:
            for(int i = 1; i < medoid.size(); i++){

                // Initialize a match value to zero
                double matchValue = 0;

                // Establish a website to test as the new center for the medoid
                Website newCenter = medoid.get(i);

                // For each website in the medoid:
                for (Website website : medoid){

                    // Skip if the website URL matches the new center URL
                    if(!newCenter.getUrl().equals(website.getUrl())){

                        // For each word in the new center's hash table:
                        for(Word word : newCenter.getWords().getKeys()){

                            // Get the word as a string
                            String nextWord = word.getWord();

                            // If the content of website contains the specified word:
                            if (website.getWords().contains(nextWord)) {

                                // Get the weighted value for the word in the new center's document
                                double newCenterTfIdf = newCenter.getWords().getWeight(word.getWord());

                                // Get the weighted value for the word in the website's document
                                double websiteTfIdf = website.getWords().getWeight(word.getWord());

                                // Multiply the weights to establish a match value, then add it to the total match value
                                // for the website
                                matchValue += newCenterTfIdf * websiteTfIdf;
                            }
                        }

                    }

                    // If the match value exceeds our best match so far, save the match value and set the new center as
                    // the best match
                    if(matchValue > bestMatchValue){
                        bestMatchValue = matchValue;
                        bestMatch = newCenter;
                    }
                }
            }

            // Remove the best-matching center from the medoid and re-add it as the first element of the medoid
            if(!medoid.get(0).getUrl().equals(bestMatch.getUrl())){
                medoid.remove(bestMatch);
                medoid.add(0, bestMatch);
            }
        }
    }
}

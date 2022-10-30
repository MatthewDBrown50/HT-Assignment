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

    private final static int NUMBER_OF_CENTERS = 7;
    private final List<Website> websites;
    private final ArrayList<ArrayList<Website>> medoids;

    public KMedoidsProducer(List<Website> websites){
        this.websites = websites;
        medoids = new ArrayList<>();
    }

    public ArrayList<ArrayList<Website>> getMedoids(){
        initializeMedoids();
        populateMedoids();
        return this.medoids;
    }

    private void initializeMedoids(){

        Random random = new Random();

        for(int i = 0; i < NUMBER_OF_CENTERS; i++){

            int index = random.nextInt(websites.size());

            medoids.add(new ArrayList<>());
            medoids.get(i).add(websites.get(index));

            websites.remove(index);
        }
    }

    private void populateMedoids(){

        for(Website website : websites){

            double matchValue = 0;
            int placementIndex = 0;
            ArrayList<Word> words = website.getWords().getKeys();

            for(int i = 0; i < NUMBER_OF_CENTERS; i++){

                // Initialize a value that measures how well content of websites.get(i) matches the
                // user-entered website's content
                double newMatchValue = 0;

                for (Word word : words) {
                    String nextWord = word.getWord();

                    // If the content of websites.get(i) contains the specified word in the user's content:
                    if (this.medoids.get(i).get(0).getWords().contains(nextWord)) {

                        // Get the weighted value for the word in the user's document
                        double userSiteTfIdf = website.getWords().getWeight(word.getWord());

                        // Get the weighted value for the word in the websites.get(i) document
                        double websiteTfIdf = this.medoids.get(i).get(0).getWords().getWeight(word.getWord());

                        // Multiply the weights to establish a match value, then add it to the total match value for the
                        // websites.get(i) document
                        double currentMatchValue = userSiteTfIdf * websiteTfIdf;

                        newMatchValue += currentMatchValue;
                    }

                }

                // If the content of websites.get(i) is a better match for the user's content than the previously stored
                // match, then overwrite these values
                if(newMatchValue >= matchValue){
                    matchValue = newMatchValue;
                    placementIndex = i;
                }
            }

            medoids.get(placementIndex).add(website);
        }
    }
}

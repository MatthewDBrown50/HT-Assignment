//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.controller;

import com.codenamebear.model.HT;
import com.codenamebear.model.Website;
import com.codenamebear.model.Word;
import com.codenamebear.utility.Cache;
import com.codenamebear.utility.KMedoidsProducer;
import com.codenamebear.utility.WebTextProcessor;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final List<Website> websites;
    private Website userWebsite;
    private final ArrayList<String> ignoredWords;
    private final int numberOfMedoids;
    private ArrayList<ArrayList<String>> medoidsList;

    public Controller() throws IOException {

        // Establish the number of medoids
        this.numberOfMedoids = 7;

        // Establish a list of ignored words
        List<String> ignoredWordsList = Files.readAllLines(Paths.get("src/main/resources/filter.txt"), StandardCharsets.UTF_8);
        this.ignoredWords = (ArrayList<String>) ignoredWordsList;

        // Establish an arraylist of website objects
        this.websites = new ArrayList<>();

    }

    public void addWebsite(String url) throws IOException {
        Website website = new Website(url);

        // Extract the text content from the webpage
        String content = WebTextProcessor.extractTextFromUrl(url);

        // Get the total number of words, along with the word count for each word in the text content,
        // then assign them to the website object
        WebTextProcessor.setWordCounts(content, website, this.ignoredWords);

        this.websites.add(website);
        this.userWebsite = website;
    }

    public String processUserRequest(String url) throws IOException {

        Website website = new Website(url);

        // Extract the text content from the webpage
        String content = WebTextProcessor.extractTextFromUrl(url);

        this.userWebsite = website;

        // Provide the WebTextProcessor with previously stored idf values
        HT idfValues = new HT(null);
        try {
            FileInputStream fileIn = new FileInputStream("src/main/resources/idfvalues.ser");
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);
            idfValues = (HT) inputStream.readObject();
            inputStream.close();
            fileIn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        WebTextProcessor.setIdfCounts(idfValues);

        // Get the total number of words, along with the word count for each word in the text content,
        // then assign them to the website object
        WebTextProcessor.setWordCounts(content, website, this.ignoredWords);

        // Provide weights to the words tracked for the user-entered URL
        HT weightedWords = WebTextProcessor.getWeightedWords(this.userWebsite, 100);
        this.userWebsite.setWords(weightedWords);

        // TODO: Reminder - Last time I checked, the weighted words for this.userWebsite showed
        //                  strange values (some words had a value of 0.0)

        // Deserialize medoid url lists and add them to this.medoidsList
        this.medoidsList = new ArrayList<>();

        for(int i = 0; i < numberOfMedoids; i++){
            String path = "src/main/resources/medoids/medoid" + i + ".ser";
            try {
                FileInputStream fileIn = new FileInputStream(path);
                ObjectInputStream inputStream = new ObjectInputStream(fileIn);
                ArrayList<String> urlList = (ArrayList<String>) inputStream.readObject();
                this.medoidsList.add(urlList);
                inputStream.close();
                fileIn.close();

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        // Provide the user with the URL whose content best matches that of the user-entered URL
        return report();
    }

    private String report(){

        Cache cache = new Cache();

        // Get the weighted words associated with the user-entered URL and store them in an arraylist
        ArrayList<Word> words = this.userWebsite.getWords().getKeys();

        // Initialize the 'best matching' website
        String bestMatch = this.medoidsList.get(0).get(0);

        // Initialize a match value, to be overwritten each time a better match is found
        double matchValue = 0;

        // Initialize the medoid index, which will keep track of which medoid is the best match to the user's url
        int medoidIndex = 0;

        // For each center in this.medoids:
        for(int i = 0; i < this.numberOfMedoids; i++) {

            String url = this.medoidsList.get(i).get(0);

            // Ignore URLs in the websites.txt document that match the user-entered URL
            if(url.equals(this.userWebsite.getUrl())){

                HT medoidCenter = cache.getWebsite(url);

                // Initialize a value that measures how well content of websites.get(i) matches the
                // user-entered website's content
                double newMatchValue = getMatchValue(words, medoidCenter);

                // If the content of websites.get(i) is a better match for the user's content than the previously stored
                // match, then overwrite these values
                if(newMatchValue >= matchValue){
                    bestMatch = medoidCenter.getUrl();
                    medoidIndex = i;
                    matchValue = newMatchValue;
                }
            }
        }

        ArrayList<String> medoid = this.medoidsList.get(medoidIndex);

        for(int i = 1; i < medoid.size(); i++){
            HT website = cache.getWebsite(medoid.get(i));

            if(!website.getUrl().equals(this.userWebsite.getUrl())){
                // Initialize a value that measures how well content of websites.get(i) matches the
                // user-entered website's content
                double newMatchValue = getMatchValue(words, website);

                // If the content of websites.get(i) is a better match for the user's content than the previously stored
                // match, then overwrite these values
                if(newMatchValue >= matchValue){
                    bestMatch = medoid.get(i);
                    matchValue = newMatchValue;
                }
            }
        }

        return "Best Match is: " + bestMatch;
    }

    private double getMatchValue(ArrayList<Word> words, HT website){

        double matchValue = 0;

        for (Word word : words) {
            String nextWord = word.getWord();

            // If the content of websites.get(i) contains the specified word in the user's content:
            if (website.contains(nextWord)) {

                // Get the weighted value for the word in the user's document
                double userSiteTfIdf = this.userWebsite.getWords().getWeight(word.getWord());

                // Get the weighted value for the word in the websites.get(i) document
                double websiteTfIdf = website.getWeight(word.getWord());

                // Multiply the weights to establish a match value, then return it
                matchValue += userSiteTfIdf * websiteTfIdf;
            }
        }

        return matchValue;
    }

    public void scrapeContent(){

        try {
            // Store a list of URL addresses from the 'websites.txt' resource
            List<String> urls = Files.readAllLines(Paths.get("src/main/resources/websites.txt"), StandardCharsets.UTF_8);

            // For the webpage at each URL:
            for(String url : urls){

                // Extract the text from the page, process word counts, and add the website to this.websites
                addWebsite(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Establish a Hash Table for each website with weight values for the words
        setTfIdfValues();

        // Serialize the idf values
        String path = "src/main/resources/idfvalues.ser";
        try{
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(WebTextProcessor.getIdfCounts());
            out.close();
            fileOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        // Serialize the websites
        for(Website website : this.websites){
            String url = website.getUrl().replaceAll("/", " ");
            url = url.replaceAll(":", "!");
            String sitePath = "src/main/resources/hashtables/" + url + ".ser";
            try{
                FileOutputStream fileOut = new FileOutputStream(sitePath);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(website.getWords());
                out.close();
                fileOut.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        // Establish a categorized list of website lists
        KMedoidsProducer kMedoidsProducer = new KMedoidsProducer(this.websites, this.numberOfMedoids);
        ArrayList<ArrayList<Website>> medoids = kMedoidsProducer.getMedoids();

        // Serialize a list urls for each medoid
        for(int i = 0; i < medoids.size(); i++){
            ArrayList<String> urls = new ArrayList<>();

            for (Website website : medoids.get(i)){
                urls.add(website.getUrl());
            }

            try{
                String centerUrlsPath = "src/main/resources/medoids/medoid" + i + ".ser";
                FileOutputStream fileOut = new FileOutputStream(centerUrlsPath);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(urls);
                out.close();
                fileOut.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setTfIdfValues(){

        for(Website website : this.websites){

            // Assign a Hash Table to each website with the words that appear on the page and their weighted values
            HT ht = WebTextProcessor.getWeightedWords(website, this.websites.size());
            website.setWords(ht);
        }
    }

    public boolean validateUrl(String address){
        try {
            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            conn.connect();
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}

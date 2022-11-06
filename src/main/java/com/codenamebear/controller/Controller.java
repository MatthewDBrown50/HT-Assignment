//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.controller;

import com.codenamebear.model.HT;
import com.codenamebear.model.Website;
import com.codenamebear.model.Word;
import com.codenamebear.utility.KMedoidsProducer;
import com.codenamebear.utility.WebTextProcessor;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {

    private final List<Website> websites;
    private Website userWebsite;
    private final ArrayList<String> ignoredWords;
    private KMedoidsProducer kMedoidsProducer;
    private ArrayList<ArrayList<Website>> medoids;

    public Controller() throws IOException {

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

        // Get the total number of words, along with the word count for each word in the text content,
        // then assign them to the website object
        WebTextProcessor.setWordCounts(content, website, this.ignoredWords);

        this.userWebsite = website;

        // Provide weights to the words tracked for the user-entered URL
        HT weightedWords = WebTextProcessor.getWeightedWords(this.userWebsite, this.websites);
        this.userWebsite.setWords(weightedWords);

        // Provide the user with the URL whose content best matches that of the user-entered URL
        return report();
    }

    private String report(){

        // Get the weighted words associated with the user-entered URL and store them in an arraylist
        ArrayList<Word> words = this.userWebsite.getWords().getKeys();
        Collections.sort(words);

        // Initialize the 'best matching' website
        Website bestMatch = this.websites.get(0);

        // Initialize a match value, to be overwritten each time a better match is found
        double matchValue = 0;

        // For each website in this.websites:
        for(int i = 0; i < this.websites.size() - 1; i++) {

            // Ignore URLs in the websites.txt document that match the user-entered URL
            if(!this.websites.get(i).getUrl().equals(this.userWebsite.getUrl())){

                // Initialize a value that measures how well content of websites.get(i) matches the
                // user-entered website's content
                double newMatchValue = 0;

                for (Word word : words) {
                    String nextWord = word.getWord();

                    // If the content of websites.get(i) contains the specified word in the user's content:
                    if (this.websites.get(i).getWords().contains(nextWord)) {

                        // Get the weighted value for the word in the user's document
                        double userSiteTfIdf = this.userWebsite.getWords().getWeight(word.getWord());

                        // Get the weighted value for the word in the websites.get(i) document
                        double websiteTfIdf = this.websites.get(i).getWords().getWeight(word.getWord());

                        // Multiply the weights to establish a match value, then add it to the total match value for the
                        // websites.get(i) document
                        double currentMatchValue = userSiteTfIdf * websiteTfIdf;

                        newMatchValue += currentMatchValue;
                    }
                }

                // If the content of websites.get(i) is a better match for the user's content than the previously stored
                // match, then overwrite these values
                if(newMatchValue >= matchValue){
                    bestMatch = websites.get(i);
                    matchValue = newMatchValue;
                }
            }
        }

        return "Best Match is: " + bestMatch.getUrl();
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

        // Establish a Hash Table for the website with weight values for the words
        setTfIdfValues();

        // Establish a categorized list of website lists
        kMedoidsProducer = new KMedoidsProducer(this.websites);
        this.medoids = kMedoidsProducer.getMedoids();
    }

    private void setTfIdfValues(){

        for(Website website : this.websites){

            // Assign a Hash Table to each website with the words that appear on the page and their weighted values
            HT ht = WebTextProcessor.getWeightedWords(website, this.websites);
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

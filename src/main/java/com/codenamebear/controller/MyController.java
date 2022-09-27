package com.codenamebear.controller;

import com.codenamebear.model.Website;
import com.codenamebear.model.WeightedWord;
import com.codenamebear.utility.WebsiteParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MyController {

    private final List<Website> websites;
    private final List<String> ignoredWords;
    Website userWebsite;

    public MyController(List<Website> websites, List<String> ignoredWords) throws IOException {
        this.websites = websites;
        this.ignoredWords = ignoredWords;

        String url = getUrlFromUser();
        addWebsite(url);
        setTfIdfValues();
        report();
    }

    private void report(){

        ArrayList<WeightedWord> weightedWords = this.userWebsite.getWeightedWords().getKeys();

        Collections.sort(weightedWords);

        Website bestMatch = this.websites.get(0);
        double matchValue = 0;

        for(int i = 0; i < websites.size() - 1; i++) {

            int index = weightedWords.size() - 1;
            double newMatchValue = 0;

            for(int j = index; j > index -50; j--) {

                if(websites.get(i).getWeightedWords().contains(weightedWords.get(j).getWord())){

                    newMatchValue += websites.get(i).getWeightedWords().getValue(weightedWords.get(j).getWord());

                }

            }

            if(newMatchValue > matchValue){
                bestMatch = websites.get(i);
                matchValue = newMatchValue;
            }

        }

        System.out.println("Best Match is: " + bestMatch.getUrl());



    }

    public static String getUrlFromUser(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter a URL:  ");
        return scanner.next();
    }

    public void addWebsite(String url) throws IOException {
        Website website = new Website(url);

        // Extract the text content from the webpage
        String content = WebsiteParser.extractTextFromUrl(url);

        // Get the total number of words, along with the word count for each word in the text content,
        // then assign them to the website object
        WebsiteParser.setWordCounts(content, website, ignoredWords);

        this.websites.add(website);

        this.userWebsite = website;
    }

    private void setTfIdfValues(){

        for(Website website : this.websites){

            website.setWeightedWords(WebsiteParser.getWeightedWords(website, this.websites));

        }
    }
}

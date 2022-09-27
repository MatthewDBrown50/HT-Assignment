package com.codenamebear.controller;

import com.codenamebear.model.HT;
import com.codenamebear.model.Website;
import com.codenamebear.model.WeightedWord;
import com.codenamebear.model.WordCount;
import com.codenamebear.utility.WebsiteParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.codenamebear.utility.WebsiteParser.extractTextFromUrl;
import static com.codenamebear.utility.WebsiteParser.setWordCounts;

public class Main {

    public static void main(String[] args) throws IOException {

        // Establish a list of ignored words
        List<String> ignoredWords = Files.readAllLines(Paths.get("src/main/resources/filter.txt"), StandardCharsets.UTF_8);

        // Store a list of URL addresses from the 'websites.txt' resource
        List<String> urls = Files.readAllLines(Paths.get("src/main/resources/websites.txt"), StandardCharsets.UTF_8);

        // Establish an arraylist of website objects
        ArrayList<Website> websites = new ArrayList<>();

        // For the webpage at each URL:
        for(String url : urls){
            Website website = new Website(url);

            // Extract the text content from the webpage
            String content = WebsiteParser.extractTextFromUrl(url);

            // Get the total number of words, along with the word count for each word in the text content,
            // then assign them to the website object
            WebsiteParser.setWordCounts(content, website, ignoredWords);

            websites.add(website);
        }

        new MyController(websites, ignoredWords);

    }



}

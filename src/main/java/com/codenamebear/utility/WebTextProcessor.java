package com.codenamebear.utility;

import com.codenamebear.model.GraphNode;
import com.codenamebear.model.HT;
import com.codenamebear.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class WebTextProcessor {

    private static ArrayList<String> ignoredWords;
    private static final HT idfCounts = new HT(null);

    public static HT getWeightedWords(GraphNode node, int numberOfWebsites){

        // For all the WordCount objects held by the Website object:
        for(Word word : node.getValues().getKeys()){

            // Calculate the word's TFIDF value and store it in the Hash Table
            double tfIdfValue = getTfIdf(word, node.getTotalWords(), numberOfWebsites);

            node.getValues().setWeight(word.getWord(), tfIdfValue);

        }

        return  node.getValues();
    }

    // Perform TFIDF final calculation
    public static double getTfIdf(Word wordCount, int totalWords, int numberOfWebsites) {

        return  tf(wordCount, totalWords) * idf(numberOfWebsites, wordCount.getWord());
    }

    public static String extractTextFromUrl(String url) throws IOException {

        // Scrape the webpage at the specified URL
        Document document;
        try{
            document = Jsoup.connect(url).get();
        } catch (Exception e){
            return "";
        }

        // Store the contents of all <p> elements on the webpage
        Elements links = document.select("p");

        // Combine the contents from the <p> elements into a single string
        StringBuilder sb = new StringBuilder();
        for(Element link : links){
            sb.append(link.text());
            sb.append(" ");
        }

        // Return the string
        return sb.toString();

    }

    public static void setWordCounts(String text, GraphNode node, ArrayList<String> wordsToIgnore) {

        // Establish list of ignored words
        ignoredWords = wordsToIgnore;

        // Establish wordCount arraylist
        HT words = new HT(node.getUrl());

        // Remove all special characters from the text
        text = text.replaceAll("[^a-zA-Z0-9]", " ");

        // Separate the words by spaces and store them into an array
        String[] textWords = text.split("\\s");

        int totalWords = 0;

        // For each word in the array:
        for(String textWord : textWords){

            // If textWord actually contains a word:
            if(!textWord.equalsIgnoreCase(" ")){

                if(!filter(textWord)){

                    // Convert textWord to lowercase
                    String lowerCaseWord = textWord.toLowerCase();

                    // Increment the total number of words for the page
                    totalWords++;

                    // If the word is found:
                    if(words.contains(lowerCaseWord)){

                        int count = words.getCount(lowerCaseWord) + 1;
                        // Add 1 to the count
                        words.setCount(lowerCaseWord, count);

                    } else {

                        // Add the word to the wordCount arraylist;
                        words.add(lowerCaseWord);

                    }
                }

            }

        }

        // Contribute node's words to the idf count
        for(Word word : words.getKeys()){
            if(idfCounts.contains(word.getWord())){

                idfCounts.setCount(word.getWord(), idfCounts.getCount(word.getWord()) + 1);

            } else {

                idfCounts.add(word.getWord());
            }
        }

        // Set the node object's wordCounts and totalWords
        node.setValues(words);
        node.setTotalWords(totalWords);
    }

    private static boolean filter(String word){

        // Return true if word is less than 3 characters and contains only numbers
        if(word.length() < 3){
            boolean hasLetters = false;

            for(int i = 0; i < word.length(); i++){
                if(Character.isAlphabetic(word.charAt(i))){
                    hasLetters = true;
                }
            }

            if(!hasLetters){
                return true;
            }
        }

        // Return true if the word is found in the list of words to be ignored
        return ignoredWords.contains(word);
    }

    public static double tf(Word wordCount, int totalWords) {
        // Return how often the word appears in the document as a quantile
        return (double) wordCount.getCount()/totalWords;
    }

    private static double idf(int numberOfWebsites, String word) {

        double count = idfCounts.getCount(word);

        return Math.log((double) numberOfWebsites/count);
    }
}

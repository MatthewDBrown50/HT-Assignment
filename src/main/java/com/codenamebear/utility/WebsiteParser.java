package com.codenamebear.utility;

import com.codenamebear.model.HT;
import com.codenamebear.model.Website;
import com.codenamebear.model.WordCount;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebsiteParser {

    public static HT getWeightedWords(Website website, List<Website> websites){
        HT weightedWords = new HT();

        for(WordCount wordCount : website.getWordCounts()){

            double tfIdfValue = getTfIdf(wordCount, website.getTotalWords(), websites);

            weightedWords.add(wordCount.getWord(), tfIdfValue);

        }

        return weightedWords;
    }

    public static double getTfIdf(WordCount wordCount, int totalWords, List<Website> websites) {
        return tf(wordCount, totalWords) * idf(websites, wordCount.getWord());
    }

    public static String extractTextFromUrl(String url) throws IOException {

        // Connect to the URL
        Document document = Jsoup.connect(url).get();

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

    public static void setWordCounts(String text, Website website, List<String> ignoredWords){

        // Establish wordCount arraylist
        ArrayList<WordCount> wordCounts = new ArrayList<>();

        // Remove all special characters from the text
        text = text.replaceAll("[^a-zA-Z0-9]", " ");

        // Separate the words by spaces and store them into an array
        String[] textWords = text.split("\\s");

        int totalWords = 0;

        // For each word in the array:
        for(String textWord : textWords){

            // If textWord actually contains a word:
            if(!textWord.equals("")){

                String lowerCaseWord = textWord.toLowerCase();

                if(!filter(lowerCaseWord, ignoredWords)){

                    totalWords++;
                    boolean wordFound = false;

                    // For each wordCount in the arraylist:
                    for(WordCount wordCount : wordCounts){

                        // If the word is found:
                        if(wordCount.getWord().equals(lowerCaseWord)){

                            // Add 1 to the count
                            wordCount.setCount(wordCount.getCount() + 1);
                            wordFound = true;
                            break;

                        }

                    }

                    // If the word wasn't found:
                    if(!wordFound){

                        // Add the word to the wordCount arraylist;
                        wordCounts.add(new WordCount(lowerCaseWord));

                    }

                }

            }

        }

        // Set the website object's wordCounts and totalWords
        website.setWordCounts(wordCounts);
        website.setTotalWords(totalWords);
    }

    private static boolean filter(String word, List<String> ignoredWords){

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

        return ignoredWords.contains(word);

    }

    private static double tf(WordCount wordCount, int totalWords) {
        return (double) wordCount.getCount()/totalWords;
    }

    private static double idf(List<Website> websites, String word) {
        double count = 0;
        double totalWords = 0;

        for(Website website : websites){
            for(WordCount wordCount: website.getWordCounts()){
                if(wordCount.getWord().equalsIgnoreCase(word)){
                    count += wordCount.getCount();
                }
            }

            totalWords += website.getTotalWords();
        }
        return Math.log(totalWords / count);
    }
}

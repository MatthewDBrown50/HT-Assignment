//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.utility;

import com.codenamebear.model.HT;
import com.codenamebear.model.Website;
import com.codenamebear.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;

public class WebTextProcessor {

    public static HT getWeightedWords(Website website, List<Website> websites){

        // For all the WordCount objects held by the Website object:
        for(Word word : website.getWords().getKeys()){

            // Calculate the word's TFIDF value and store it in the Hash Table
            double tfIdfValue = getTfIdf(word, website.getTotalWords(), websites);

            website.getWords().setWeight(word.getWord(), tfIdfValue);

        }

        return  website.getWords();
    }

    // Perform TFIDF final calculation
    public static double getTfIdf(Word wordCount, int totalWords, List<Website> websites) {

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
        HT words = new HT();

        // Remove all special characters from the text
        text = text.replaceAll("[^a-zA-Z0-9]", " ");

        // Separate the words by spaces and store them into an array
        String[] textWords = text.split("\\s");

        int totalWords = 0;

        // For each word in the array:
        for(String textWord : textWords){

            // If textWord actually contains a word:
            if(!textWord.equals("")){

                // Convert textWord to lowercase
                String lowerCaseWord = textWord.toLowerCase();

                // If the lowercase word is not in the list of filtered words:
                if(!filter(lowerCaseWord, ignoredWords)){

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

        // Set the website object's wordCounts and totalWords
        website.setWords(words);
        website.setTotalWords(totalWords);
    }

    private static boolean filter(String word, List<String> ignoredWords){

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

    private static double tf(Word wordCount, int totalWords) {

        // Return how often the word appears in the document as a quantile
        return (double) wordCount.getCount()/totalWords;
    }

    private static double idf(List<Website> websites, String word) {
        double count = 0;
        double totalWords = 0;

        for(Website website : websites){

            // Count how many times the word appears in the document
            for(Word nextWord: website.getWords().getKeys()){

                if(nextWord.getWord().equalsIgnoreCase(word)){
                    count += nextWord.getCount();
                }
            }

            // Track the total number of words between all documents
            totalWords += website.getTotalWords();
        }

        return Math.log(totalWords / count);
    }
}

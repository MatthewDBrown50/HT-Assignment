package com.codenamebear.model;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class PersistentHT {

    // Establish bytes-per-line for indexing data
    private static final int MAX_INT_STRING_SIZE = 10;
    private final int MAX_WORD_STRING_SIZE = 25;
    private final int CHARS_PER_LINE = MAX_INT_STRING_SIZE + MAX_WORD_STRING_SIZE;
    private final int BYTES_PER_LINE = CHARS_PER_LINE + 2;

    // Add a new word to the table
    public void add(String word, int count) throws IOException {

        // Establish a truncated hashcode to serve as index factor
        int h = Math.abs(word.hashCode()) % 1000000;

        // Convert 'word' and 'count' variables into a single line of data
        String data = createData(word, count);

        // Establish initial file to attempt to write data to
        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);

        // Establish boolean to track whether the data has been written to a file
        boolean written = false;

        // Establish a file number that will be used to create new files as necessary
        int fileNumber = 0;

        // Create the file if it doesn't already exist
        if(!file.exists()){
            createFile(filePath);
        }

        // While the data has not yet been written to a file:
        while(!written){

            // Establish a RandomAccessFile (RAF) to handle reading and writing
            try(RandomAccessFile raf = new RandomAccessFile(file, "rw")){

                // Point the RAF to the index determined by the hashcode
                raf.seek((long) BYTES_PER_LINE * h);

                try {
                    // Attempt to read the first character of the data at the current index
                    byte firstByte = raf.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    // If the character is a number or letter:
                    if(Character.isAlphabetic(firstLetter) || Character.isDigit(firstLetter)){

                        // Data already exists here
                        // Prepare to attempt insertion in the next file
                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);

                        // If the next file doesn't already exist, then create it
                        if(!file.exists()){

                            createFile(filePath);

                        }
                    } else {

                        // Write the data to the current index
                        raf.seek((long) BYTES_PER_LINE * h);
                        raf.write(data.getBytes());
                        raf.writeBytes(System.getProperty("line.separator"));
                        written = true;

                    }
                } catch (Exception e){
                    // This catch is reached when the current index exceeds the previous length of the file, which
                    // means the index does not already contain data
                    // Write the data to the current index
                    raf.seek((long) BYTES_PER_LINE * h);
                    raf.write(data.getBytes());
                    raf.writeBytes(System.getProperty("line.separator"));
                    written = true;
                }
            }
        }
    }

    // Convert 'word' and 'count' variables into a single line of data, then return the data String
    public String createData(String word, int count){
        String countString = Integer.toString(count);

        String wordToStore = word + " ".repeat(Math.max(0, MAX_WORD_STRING_SIZE - word.length()));

        String countToStore = countString + " ".repeat(Math.max(0, MAX_INT_STRING_SIZE - countString.length()));

        return wordToStore + countToStore;
    }

    // Return the count value associated with the specified word
    public int getCount(String word){

        // Establish a truncated hashcode to serve as index factor
        int h = Math.abs(word.hashCode()) % 1000000;

        // Establish initial file to search in
        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);

        // Establish a file number that will be used to search subsequent files as necessary
        int fileNumber = 0;

        // Continue searching until the word is found
        while(true){

            // Establish a RandomAccessFile (RAF) to handle reading
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){

                // Point the RAF to the index to read from
                randomAccessFile.seek((long) BYTES_PER_LINE * h);

                try{
                    // Attempt to read the first character at this index
                    byte firstByte = randomAccessFile.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    // If the first character is a letter or number:
                    if(Character.isAlphabetic(firstLetter) || Character.isDigit(firstLetter)){

                        // Extract the data from the index
                        String data = firstLetter + randomAccessFile.readLine();

                        // Split the data into words, then assign the words to a list
                        String[] splitString = data.split(" ");
                        List<String> stringList = Arrays.asList(splitString);

                        // The first word of the data will be the text word we need; assign it to a String variable
                        String extractedWord = stringList.get(0);

                        // Initialize a variable to assign the count to
                        int count = -1;

                        // Find the next non-empty string in the string list; parse it to an integer and assign it to
                        // the count variable
                        for(int i = 1; i < stringList.size(); i++){
                            if(!stringList.get(i).equals("")){
                                count = Integer.parseInt(stringList.get(i));
                            }
                        }

                        // If the text word found at the current index is the word we're looking for:
                        if(extractedWord.equals(word)){

                            // Return the extracted count value
                            return count;

                        } else {

                            // Establish the next file to search
                            fileNumber++;
                            filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                            file = new File(filePath);
                        }
                    } else {

                        // Establish the next file to search
                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);
                    }

                } catch (Exception e){
                    // This catch is reached when the current index exceeds the current length of the file, which means
                    // that data has not been written to this index
                    // Establish the next file to search
                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);
                }
            } catch (Exception e)   {
                // By design, this catch should never be reached
                e.printStackTrace();
                return -1;
            }
        }
    }

    // Increment the count value for the specified word by the specified increment value
    public void incrementCount(String word, int increment){

        // Establish a truncated hashcode to serve as index factor
        int h = Math.abs(word.hashCode()) % 1000000;

        // Establish initial file to search in
        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);

        // Establish a file number that will be used to search subsequent files as necessary
        int fileNumber = 0;

        // While there are still more files to search, and a return statement has not been reached:
        while(file.exists()){

            // Establish a RandomAccessFile (RAF) to handle reading and writing
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){

                // Point the RAF to index to read from
                randomAccessFile.seek((long) BYTES_PER_LINE * h);

                try{
                    // Attempt to read the first char of data at the current index
                    byte firstByte = randomAccessFile.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    // If the character is a letter or number:
                    if(Character.isAlphabetic(firstLetter) || Character.isDigit(firstLetter)){

                        // Extract the data at this location
                        String data = firstLetter + randomAccessFile.readLine();

                        // Split the extracted data into word strings, then assign the strings to a list
                        String[] splitString = data.split(" ");
                        List<String> stringList = Arrays.asList(splitString);

                        // Assign the first word in the list to a string variable
                        String extractedWord = stringList.get(0);

                        // If the text word at this location matches the word we're looking for:
                        if(extractedWord.equals(word)){

                            // Initialize a variable for storing the count
                            int count = -1;

                            // The next non-empty word in the list is the count; assign it to the count variable
                            for(int i = 1; i < stringList.size(); i++){
                                if(!stringList.get(i).equals("")){
                                    count = Integer.parseInt(stringList.get(i)) + increment;
                                }
                            }

                            // Create a new data string containing the word and the new count
                            String newData = createData(word, count);

                            // Write the data string to the current index and return
                            randomAccessFile.seek((long) BYTES_PER_LINE * h);
                            randomAccessFile.write(newData.getBytes());
                            randomAccessFile.writeBytes(System.getProperty("line.separator"));
                            return;

                        } else {

                            // Establish the next file to search
                            fileNumber++;
                            filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                            file = new File(filePath);
                        }
                    } else {

                        // Establish the next file to search
                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);
                    }

                } catch (Exception e){

                    // This catch is reached when the current index exceeds the current length of the file, which means
                    // that data has not been written to this index
                    // Establish the next file to search
                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);
                }
            } catch (Exception e)   {

                // This catch, by design, should never be reached
                e.printStackTrace();
            }
        }
    }

    // Determine if the table contains the specified word
    public boolean contains(String word){

        // Establish a truncated hashcode to serve as index factor
        int h = Math.abs(word.hashCode()) % 1000000;

        // Establish the initial file to search
        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);

        // Establish a file number that will be used to search subsequent files as necessary
        int fileNumber = 0;

        // While there are more files to search and a return statement has not been reached:
        while(file.exists()){

            // Establish a RandomAccessFile (RAF) to handle reading
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){

                // Point the RAF to the index to read from
                randomAccessFile.seek((long) BYTES_PER_LINE * h);

                try{
                    // Attempt to read the first char of the data at the current index
                    byte firstByte = randomAccessFile.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    // If the char is a letter or number:
                    if(Character.isAlphabetic(firstLetter) || Character.isDigit(firstLetter)){

                        // Extract the data from the current index, split it into word strings,
                        // and assign them to a list
                        String data = firstLetter + randomAccessFile.readLine();
                        String[] splitString = data.split(" ");
                        List<String> stringList = Arrays.asList(splitString);

                        // Assign the text word discovered at this index to a string variable
                        String extractedWord = stringList.get(0);

                        // If the text word is the word we're searching for:
                        if(extractedWord.equals(word)){

                            return true;

                        } else {

                            // Establish the next file to search
                            fileNumber++;
                            filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                            file = new File(filePath);
                        }
                    } else {

                        // Establish the next file to search
                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);
                    }

                } catch (Exception e){

                    // This catch is reached when the current index exceeds the current length of the file, which means
                    // that data has not been written to this index
                    // Establish the next file to search
                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);
                }
            } catch (Exception e)   {

                // This catch, by design, should never be reached
                e.printStackTrace();
            }
        }

        return false;
    }

    // Return the maximally allowed word size
    // This is called by the WebTextProcessor in order to prevent the storing of words that match or exceed the data
    // size of text words, as stored by this table
    public int getMAX_WORD_STRING_SIZE() {
        return MAX_WORD_STRING_SIZE;
    }

    // Create a new file at the specified path
    public void createFile(String filePath) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(null);
        out.close();
        fileOut.close();
    }
}

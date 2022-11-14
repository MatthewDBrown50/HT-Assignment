package com.codenamebear.model;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/******************************************
 Created on 11/13/2022 by Matthew D Brown
 *******************************************/

public class PersistentHT {

    private static final int MAX_INT_STRING_SIZE = 10;
    private final int MAX_WORD_STRING_SIZE = 25;
    private final int CHARS_PER_LINE = MAX_INT_STRING_SIZE + MAX_WORD_STRING_SIZE;
    private final int BYTES_PER_LINE = CHARS_PER_LINE + 2;

    public void add(String word, int count) throws IOException {

        int h = Math.abs(word.hashCode()) % 1000000;

        String data = createData(word, count);

        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);
        boolean written = false;
        int fileNumber = 0;

        if(!file.exists()){
            createFile(filePath);
        }

        while(!written){

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek((long) BYTES_PER_LINE * h);

            try {
                byte firstByte = raf.readByte();
                char firstLetter = (char) (firstByte & 0xFF);

                if(Character.isAlphabetic(firstLetter) || Character.isDigit(firstLetter)){

                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);

                    if(!file.exists()){

                        createFile(filePath);
                    }
                } else {


                    raf.seek((long) BYTES_PER_LINE * h);
                    raf.write(data.getBytes());
                    raf.writeBytes(System.getProperty("line.separator"));


                    written = true;
                }
            } catch (Exception e){

                raf.seek((long) BYTES_PER_LINE * h);
                raf.write(data.getBytes());
                raf.writeBytes(System.getProperty("line.separator"));
                written = true;
            }

        }
    }

    public String createData(String word, int count){
        String countString = Integer.toString(count);

        String wordToStore = word + " ".repeat(Math.max(0, MAX_WORD_STRING_SIZE - word.length()));

        String countToStore = countString + " ".repeat(Math.max(0, MAX_INT_STRING_SIZE - countString.length()));

        return wordToStore + countToStore;
    }

    public int getCount(String word){
        int h = Math.abs(word.hashCode()) % 1000000;

        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);
        int fileNumber = 0;

        while(true){

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){

                randomAccessFile.seek((long) BYTES_PER_LINE * h);

                try{

                    byte firstByte = randomAccessFile.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    if(Character.isAlphabetic(firstLetter) || Character.isDigit(firstLetter)){

                        String data = firstLetter + randomAccessFile.readLine();

                        String[] splitString = data.split(" ");

                        List<String> stringList = Arrays.asList(splitString);

                        String extractedWord = stringList.get(0);
                        int count = -1;

                        for(int i = 1; i < stringList.size(); i++){
                            if(!stringList.get(i).equals("")){
                                count = Integer.parseInt(stringList.get(i));
                            }
                        }

                        if(extractedWord.equals(word)){
                            return count;
                        } else {

                            fileNumber++;
                            filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                            file = new File(filePath);

                        }
                    } else {

                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);
                    }

                } catch (Exception e){
                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);
                }
            } catch (Exception e)   {

                e.printStackTrace();
                return -1;
            }
        }
    }

    public void incrementCount(String word, int increment){
        int h = Math.abs(word.hashCode()) % 1000000;

        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);
        int fileNumber = 0;

        while(file.exists()){

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){

                randomAccessFile.seek((long) BYTES_PER_LINE * h);

                try{

                    byte firstByte = randomAccessFile.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    if(Character.isAlphabetic(firstLetter)){

                        String data = firstLetter + randomAccessFile.readLine();

                        String[] splitString = data.split(" ");

                        List<String> stringList = Arrays.asList(splitString);

                        String extractedWord = stringList.get(0);

                        if(extractedWord.equals(word)){

                            int count = -1;

                            for(int i = 1; i < stringList.size(); i++){
                                if(!stringList.get(i).equals("")){
                                    count = Integer.parseInt(stringList.get(i)) + increment;
                                }
                            }

                            String newData = createData(word, count);

                            randomAccessFile.seek((long) BYTES_PER_LINE * h);
                            randomAccessFile.write(newData.getBytes());
                            randomAccessFile.writeBytes(System.getProperty("line.separator"));

                            return;
                        } else {
                            fileNumber++;
                            filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                            file = new File(filePath);
                        }
                    } else {

                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);
                    }

                } catch (Exception e){
                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);
                }
            } catch (Exception e)   {
                e.printStackTrace();
            }
        }

    }

    public boolean contains(String word){
        int h = Math.abs(word.hashCode()) % 1000000;

        String filePath = "src/main/resources/IdfRafs/idf.data";
        File file = new File(filePath);
        int fileNumber = 0;

        while(file.exists()){

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){

                randomAccessFile.seek((long) BYTES_PER_LINE * h);

                try{

                    byte firstByte = randomAccessFile.readByte();
                    char firstLetter = (char) (firstByte & 0xFF);

                    if(Character.isAlphabetic(firstLetter)){

                        String data = firstLetter + randomAccessFile.readLine();

                        String[] splitString = data.split(" ");

                        List<String> stringList = Arrays.asList(splitString);

                        String extractedWord = stringList.get(0);


                        if(extractedWord.equals(word)){
                            return true;
                        } else {
                            fileNumber++;
                            filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                            file = new File(filePath);
                        }
                    } else {

                        fileNumber++;
                        filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                        file = new File(filePath);
                    }

                } catch (Exception e){
                    fileNumber++;
                    filePath = "src/main/resources/IdfRafs/idf" + fileNumber + ".data";
                    file = new File(filePath);
                }
            } catch (Exception e)   {
                e.printStackTrace();
            }
        }

        return false;
    }

    public int getMAX_WORD_STRING_SIZE() {
        return MAX_WORD_STRING_SIZE;
    }

    public void createFile(String filePath) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(null);
        out.close();
        fileOut.close();
    }
}

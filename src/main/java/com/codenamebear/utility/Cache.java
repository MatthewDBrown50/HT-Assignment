package com.codenamebear.utility;

import com.codenamebear.model.HT;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/******************************************
 Created on 11/6/2022 by Matthew D Brown
 *******************************************/

public class Cache {

    static class Address {
        int i;
        int j;

        public Address(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private final static int NUMBER_OF_BLOCKS = 5;
    private final static int BLOCK_SIZE = 4;
    private final HT[][] cache;
    private final LinkedList<Address> lruQueue;
    private int itemsInQueue;
    private final static int MAX_ITEMS_IN_QUEUE = NUMBER_OF_BLOCKS*BLOCK_SIZE;

    public Cache() {
        this.cache = new HT[NUMBER_OF_BLOCKS][BLOCK_SIZE];
        this.itemsInQueue = 0;
        this.lruQueue = new LinkedList<>();
    }

    public HT getWebsite(String url){
        for(int i = 0; i < NUMBER_OF_BLOCKS; i++){
            for(int j = 0; j < BLOCK_SIZE; j++){
                if(cache[i][j] != null){
                    if(cache[i][j].getUrl().equals(url)){
                        return cache[i][j];
                    }
                }
            }
        }

        String fileUrl = url.replaceAll(":", "!");
        fileUrl = fileUrl.replaceAll("/", " ");
        String path = "src/main/resources/hashtables/" + fileUrl + ".ser";
        HT website = null;

        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);
            website = (HT) inputStream.readObject();
            inputStream.close();
            fileIn.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        if(itemsInQueue < MAX_ITEMS_IN_QUEUE){
            for(int i = 0; i < NUMBER_OF_BLOCKS; i++){
                for(int j = 0; j < BLOCK_SIZE; j++){
                    if(this.cache[i][j] == null){
                        this.cache[i][j] = website;
                        lruQueue.addFirst(new Address(i, j));
                        itemsInQueue++;
                    }
                }
            }
        } else {

            Address address = lruQueue.removeLast();

            cache[address.i][address.j] = website;

            lruQueue.addFirst(address);

        }

        return website;
    }
}

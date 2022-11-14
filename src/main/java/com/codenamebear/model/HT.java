package com.codenamebear.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class HT implements java.io.Serializable {

    private final String url;

    public HT(String url) {
        this.url = url;
    }

    static final class Node implements Serializable {
        String key;
        int count;
        double tfIdfValue;
        Node next;
        Node(String k, Node n) {
            key = k;
            count = 1;
            tfIdfValue = 0;
            next = n;
        }

        public void setTfIdfValue(double tfIdfValue) {
            this.tfIdfValue = tfIdfValue;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    private Node[] table = new Node[8];
    private int size = 0;
    
    @Serial
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);
        for (Node node : table) {
            for (Node e = node; e != null; e = e.next) {
                s.writeObject(e.key);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public boolean contains(String key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return true;
            }
        }
        return false;
    }

    public void setWeight(String key, double weight) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                e.setTfIdfValue(weight);
            }
        }
    }

    public void setCount(String key, int count) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                e.setCount(count);
            }
        }
    }

    public int getCount(String key){
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return e.count;
            }
        }
        return 0;
    }

    public double getWeight(String key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return e.tfIdfValue;
            }
        }
        return 0;
    }

    public ArrayList<Word> getKeys(){
        ArrayList<Word> words = new ArrayList<>();
        for (Node node : table) {
            Node e = node;

            if(e != null){

                Word word = new Word(e.key);
                word.setCount(e.count);
                word.setTfIdfValue(e.tfIdfValue);
                words.add(word);

                while (e.next != null) {
                    e = e.next;

                    Word nextWord = new Word(e.key);
                    word.setCount(e.count);
                    word.setTfIdfValue(e.tfIdfValue);
                    words.add(nextWord);
                }
            }
        }
        return words;
    }

    public void add(String key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return;
            }
        }
        table[i] = new Node(key, table[i]);
        ++size;
        if ((float)size/table.length >= 0.75f){
            resize();
        }
    }

    private void resize() {
        Node[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node[] newTable = new Node[newCapacity];
        for (Node node : oldTable) {
            for (Node e = node; e != null; e = e.next) {
                int h = e.key.hashCode();
                int j = h & (newTable.length - 1);

                Node n = new Node(e.key, newTable[j]);
                n.setCount(e.count);
                n.setTfIdfValue(e.tfIdfValue);

                newTable[j] = n;
            }
        }
        table = newTable;
    }
}

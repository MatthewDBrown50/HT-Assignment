package com.codenamebear.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.util.ArrayList;

public class HT implements java.io.Serializable {

    static final class Node {
        String key;
        double value;
        Node next;
        Node(String k, double v, Node n) { key = k; value = v; next = n; }
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

    public double getValue(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return e.value;
            }
        }
        return 0;
    }

    public ArrayList<WeightedWord> getKeys(){
        ArrayList<WeightedWord> weightedWords = new ArrayList<>();
        for (Node node : table) {
            Node e = node;

            if(e != null){
                weightedWords.add(new WeightedWord(e.key, e.value));

                while (e.next != null) {
                    e = e.next;
                    weightedWords.add(new WeightedWord(e.key, e.value));
                }
            }
        }
        return weightedWords;
    }

    public void add(String key, double value) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return;
            }
        }
        table[i] = new Node(key, value, table[i]);
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
                newTable[j] = new Node(e.key, e.value, newTable[j]);
            }
        }
        table = newTable;
    }
}

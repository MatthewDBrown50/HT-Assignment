package com.codenamebear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.util.ArrayList;

class HT implements java.io.Serializable {

    static final class Node {
        Object key;
        Node next;
        // int count;
        // Object value;
        Node(Object k, Node n) { key = k; next = n; }
    }

    Node[] table = new Node[8];
    int size = 0;
    
    @Serial
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);
        for (int i = 0; i < table.length; ++i) {
            for (Node e = table[i]; e != null; e = e.next) {
                s.writeObject(e.key);
            }
        }
    }

    @Serial
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int n = s.readInt();
        for (int i = 0; i < n; ++i){
            add(s.readObject());
        }
    }

    boolean contains(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Object> getKeys(){
        ArrayList<Object> keys = new ArrayList<>();
        for (Node node : table) {
            Node e = node;
            keys.add(e.key);

            while (e.next != null) {
                e = e.next;
                keys.add(e.key);
            }
        }
        return keys;
    }

    void add(Object key) {
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

    void resize() {
        Node[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node[] newTable = new Node[newCapacity];
        for (int i = 0; i < oldCapacity; ++i) {
            for (Node e = oldTable[i]; e != null; e = e.next) {
                int h = e.key.hashCode();
                int j = h & (newTable.length - 1);
                newTable[j] = new Node(e.key, newTable[j]);
            }
        }
        table = newTable;
    }

    void remove(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);

        // 1010101010
        // 1110000000
        // 101

        // a == 10110001
        // b == 11011011
        //a&b== 10010001


        Node e = table[i], p = null;
        while (e != null) {
            if (key.equals(e.key)) {
                if (p == null)
                    table[i] = e.next;
                else
                    p.next = e.next;
                break;
            }
            p = e;
            e = e.next;
        }
    }
}

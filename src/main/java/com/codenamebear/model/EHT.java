package com.codenamebear.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/******************************************
 Created on 11/10/2022 by Matthew D Brown
 *******************************************/

public class EHT {
    static class Page {
        static int PAGE_SZ = 20;
        private final Map<String, Integer> m = new HashMap<>();
        int d = 0;

        boolean full() {
            return m.size() > PAGE_SZ;
        }

        void put(String word, int count) {
            m.put(word, count);
        }

        void remove(String word){
            m.remove(word);
        }


        int get(String word) {
            return m.get(word);
        }
    }

    int gd = 0;

    List<Page> pp = new ArrayList<>();

    public EHT() {
        pp.add(new Page());
    }

    Page getPage(String word) {
        int h = word.hashCode();
        return pp.get(h & ((1 << gd) - 1));
    }

    public void put(String word, int count) {
        Page p = getPage(word);
        if (p.full() && p.d == gd) {
            List<Page> pp2 = new ArrayList<>(pp);
            pp.addAll(pp2);
            ++gd;
        }

        if (p.full() && p.d < gd) {
            p.put(word, count);
            Page p1, p2;
            p1 = new Page();
            p2 = new Page();
            for (String word2 : p.m.keySet()) {
                int count2 = p.m.get(word2);

                int h = word2.hashCode() & ((1 << gd) - 1);

                if ((h | (1 << p.d)) == h)
                    p2.put(word2, count2);
                else
                    p1.put(word2, count2);
            }

            List<Integer> l = new ArrayList<>();

            for (int i = 0; i < pp.size(); ++i)
                if (pp.get(i) == p)
                    l.add(i);

            for (int i : l)
                if ((i | (1 << p.d)) == i)
                    pp.set(i, p2);
                else
                    pp.set(i, p1);

            p1.d = p.d + 1;
            p2.d = p1.d;

        } else
            p.put(word, count);
    }

    public void incrementCount(String word){
        int count = get(word);
        count++;
        getPage(word).remove(word);
        getPage(word).put(word, count);
    }

    public boolean contains(String word){
        System.out.println("Found value of " + get(word) + " for " + word);

        return (get(word) >= 0);
    }

    public int get(String word) {
        try{
            return getPage(word).get(word);
        } catch (Exception e) {
            return -1;
        }
    }
}

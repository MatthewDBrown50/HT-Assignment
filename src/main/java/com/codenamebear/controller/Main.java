package com.codenamebear.controller;

import com.codenamebear.view.MainFrame;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        new MainFrame(new Controller());

    }
}

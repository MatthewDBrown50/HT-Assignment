package com.codenamebear.view;

import com.codenamebear.controller.Graph;
import com.codenamebear.controller.Scraper;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{

    public MainFrame(Graph graph, Scraper scraper){
        super("Related URL Finder");

        int width = 900;
        int height = 1050;

        JPanel mainPanel = new MainPanel(graph, scraper, width, height);

        setLayout(new BorderLayout());

        add(mainPanel, BorderLayout.CENTER);

        setSize(width,height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }
}
package com.codenamebear.view;

import com.codenamebear.controller.Graph;
import com.codenamebear.controller.Scraper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;


public class MainPanel extends JPanel {

    private final JTextField sourceUrlField;
    private final JTextField destinationUrlField;
    private final JLabel warningLabel;
    private final JLabel  validCheck;

    private final Graph graph;
    private final Scraper scraper;

    private final static Font BOLD_FONT = new Font("San-Serif", Font.BOLD, 14);

    public MainPanel(Graph graph, Scraper scraper) {

        this.graph = graph;
        this.scraper = scraper;

        Dimension dim = getPreferredSize();
        dim.width = 250;
        setPreferredSize(dim);

        //Source label and it's url field
        JLabel sourceLabel = new JLabel("Enter source URL here:");
        sourceLabel.setFont(BOLD_FONT);
        sourceUrlField = new JTextField();

        //Destination label and it's url field
        JLabel destinationLabel = new JLabel("Enter destination URL here:");
        destinationLabel.setFont(BOLD_FONT);
        destinationUrlField = new JTextField();

        // Button to perform search for shortest path
        JButton searchButton = new JButton("Show traversal");
        searchButton.setFont(BOLD_FONT);

        //Warning if source or destination inputs are invalid
        validCheck = new JLabel("");
        validCheck.setFont(BOLD_FONT);

        //Scrape content
        JButton scrapeButton = new JButton("Re-Scrape Content");
        scrapeButton.setFont(BOLD_FONT);

        //Time warning
        warningLabel = new JLabel("WARNING: This may take several minutes!");
        warningLabel.setFont(BOLD_FONT);

        searchButton.addActionListener(e -> {

            String sourceURL = sourceUrlField.getText();
            String destinationURL = destinationUrlField.getText();

            ArrayList<String> shortestPath = graph.getShortestPath(sourceURL, destinationURL);

            // TODO: DISPLAY THE URLS IN THE shortestPath ARRAYLIST

        });

        //Scraper
        scrapeButton.addActionListener(e -> {

            // Scrape web content for all URLs
            try {
                scraper.scrapeContent();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            scrapeButton.setText("Re-Scrape Content");

            //TODO Make the 1000 website choices appear in the bottom panel if they are not already loaded
            //Show website choices in bottom panel


        });

        Border innerBorder = BorderFactory.createBevelBorder(1);
        Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        //Scrape button
        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = .3;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(20, 30, 0, 5);
        add(scrapeButton, gc);

        // Warning label next to scraper
        gc.gridx = 2;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = .3;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(20, 5, 0, 30);
        add(warningLabel, gc);

        //Source label
        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 0, 30);
        gc.anchor = GridBagConstraints.LINE_START;
        add(sourceLabel, gc);

        //Source text input
        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 30, 0, 30);
        add(sourceUrlField, gc);

        //destination label
        gc.gridx = 1;
        gc.gridy = 4;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 0, 30);
        gc.anchor = GridBagConstraints.LINE_START;
        add(destinationLabel, gc);

        //Label showing where to place source
        gc.gridx = 1;
        gc.gridy = 5;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 0, 30);
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(destinationUrlField, gc);


        //Button to begin Dijkstra, will output results when done
        gc.gridx = 1;
        gc.gridy = 6;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(10, 30, 0, 30);
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(searchButton, gc);

        //Label to explain if URL's are not valid
        gc.gridx = 1;
        gc.gridy = 7;
        gc.gridwidth = 2;
        gc.weightx = .5;
        gc.weighty = .5;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 0, 30);
        add(validCheck, gc);


    }
}
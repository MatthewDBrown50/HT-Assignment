package com.codenamebear.view;

import com.codenamebear.controller.Controller;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class MainPanel extends JPanel {

    private final JTextField sourceUrlField;
    private final JTextField destinationUrlField;
    private final JLabel warningLabel;
    private final JLabel  validCheck;
    //Replaced by bottom panel
    //private final JList<String> resultsList;
    private final Controller controller;
    private final static Font BOLD_FONT = new Font("San-Serif", Font.BOLD, 14);
    private final static Font PLAIN_FONT = new Font("San-Serif", Font.PLAIN, 14);

    public MainPanel(Controller controller) {

        this.controller = controller;

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

        validCheck = new JLabel("");
        validCheck.setFont(BOLD_FONT);

        //Button to scrape data
        JButton scrapeButton = new JButton("Scrape Content");
        scrapeButton.setFont(BOLD_FONT);

        warningLabel = new JLabel("WARNING: This may take several minutes!");
        warningLabel.setFont(BOLD_FONT);

        searchButton.addActionListener(e -> {

            // Carry out user request
            performSearch();

        });

        //Scraper
        scrapeButton.addActionListener(e -> {

            // Scrape web content for all URLs
            controller.scrapeContent();
            scrapeButton.setText("Re-Scrape Content");


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

        //Label showing where to place source
        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10, 30, 0, 30);
        gc.anchor = GridBagConstraints.LINE_START;
        add(sourceLabel, gc);

        //Source input text field
        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(10, 30, 5, 30);
        add(sourceUrlField, gc);

        //Label showing where to place destination
        gc.gridx = 1;
        gc.gridy = 4;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10, 30, 0, 30);
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(destinationLabel, gc);

        //Destination input text field
        gc.gridx = 1;
        gc.gridy = 5;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(10, 30, 10, 30);
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
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(15, 30, 15, 30);
        add(validCheck, gc);


    }

    private void performSearch() {
    //Some of this may change as controller is changed

        // Make sure the URLs are valid
        String sourceUrl = sourceUrlField.getText();
        boolean validUrl = controller.validateUrl(sourceUrl);
        String destinationUrl = destinationUrlField.getText();
        boolean validUrl2 = controller.validateUrl(destinationUrl);

        // If the URLs are valid:
        if (validUrl && validUrl2) {
            
            // call graph traversal method
            
        } else {
            validCheck.setText("Invalid URL! Please try again.");
        }
    }
}
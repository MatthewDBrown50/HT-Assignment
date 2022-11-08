//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.view;

import com.codenamebear.controller.Controller;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainPanel extends JPanel {

    private final JTextField urlField;
    private final JLabel resultLabel;
    private final JLabel otherResultsLabel;
    private final JList<String> resultsList;
    private final Controller controller;
    private final static Font BOLD_FONT = new Font("San-Serif", Font.BOLD, 14);
    private final static Font PLAIN_FONT = new Font("San-Serif", Font.PLAIN, 14);

    public MainPanel(Controller controller) {

        this.controller = controller;

        Dimension dim = getPreferredSize();
        dim.width = 250;
        setPreferredSize(dim);

        JLabel instructionLabel = new JLabel("Enter URL here:");
        instructionLabel.setFont(BOLD_FONT);

        urlField = new JTextField();

        JButton searchButton = new JButton("Search for Related URL");
        searchButton.setFont(BOLD_FONT);

        resultLabel = new JLabel("Results will appear here.");
        resultLabel.setFont(BOLD_FONT);

        otherResultsLabel = new JLabel("More related URLs:");
        otherResultsLabel.setFont(BOLD_FONT);
        otherResultsLabel.setVisible(false);

        resultsList = new JList<>();
        resultsList.setFont(PLAIN_FONT);
        resultsList.setBackground(new Color(238, 238, 238));
        resultsList.setVisible(false);

        JButton scrapeButton = new JButton("Re-Scrape Content");
        scrapeButton.setFont(BOLD_FONT);

        JLabel warningLabel = new JLabel("This will delete and recreate all serialized files");
        warningLabel.setFont(BOLD_FONT);

        searchButton.addActionListener(e -> {

            // Carry out user request
            performSearch();

        });

        scrapeButton.addActionListener(e -> {

            // Scrape web content for all URLs
            controller.scrapeContent();

        });

        Border innerBorder = BorderFactory.createBevelBorder(1);
        Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .5;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 0, 30);
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(instructionLabel, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(10, 30, 10, 30);
        add(urlField, gc);

        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(0, 30, 0, 30);
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(searchButton, gc);

        gc.gridx = 1;
        gc.gridy = 4;
        gc.gridwidth = 2;
        gc.weightx = .5;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(30, 30, 30, 30);
        add(resultLabel, gc);

        gc.gridx = 1;
        gc.gridy = 5;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 10, 30);
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(otherResultsLabel, gc);

        gc.gridx = 1;
        gc.gridy = 6;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 50, 30);
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(resultsList, gc);

        gc.gridx = 1;
        gc.gridy = 7;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(0, 30, 0, 5);
        add(scrapeButton, gc);

        gc.gridx = 2;
        gc.gridy = 7;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 5, 0, 30);
        add(warningLabel, gc);

    }

    private void performSearch() {

        // Make sure the URL is valid
        String url = urlField.getText();
        boolean validUrl = controller.validateUrl(url);

        // If the URL is valid:
        if (validUrl) {
            try {
                // Have controller process the request, then provide the result to the user
                String[] results = controller.processUserRequest(url);
                resultLabel.setText("Best Match is: " + results[0]);
                otherResultsLabel.setVisible(true);

                String[] modifiedResults = Arrays.copyOfRange(results, 1, results.length);
                resultsList.setListData(modifiedResults);
                resultsList.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            resultLabel.setText("Invalid URL! Please try again.");
            otherResultsLabel.setVisible(false);
            otherResultsLabel.setVisible(false);
        }
    }
}
//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.view;

import com.codenamebear.controller.Controller;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;

public class MainPanel extends JPanel {

    private final JTextField urlField;
    private final JLabel resultLabel;
    private final Controller controller;
    private final JLabel warningLabel;

    public MainPanel(Controller controller) {

        this.controller = controller;

        Dimension dim = getPreferredSize();
        dim.width = 250;
        setPreferredSize(dim);

        JLabel instructionLabel = new JLabel("Enter URL here:");
        urlField = new JTextField();
        JButton searchButton = new JButton("Search for Related URL");
        resultLabel = new JLabel("Result will appear here.");
        JButton scrapeButton = new JButton("Re-Scrape Content");
        warningLabel = new JLabel("WARNING: This will take several minutes!");

        searchButton.addActionListener(e -> {

            // Carry out user request
            performSearch();

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
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(50, 30, 0, 30);
        add(instructionLabel, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 0, 30);
        add(urlField, gc);

        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(0, 30, 0, 30);
        add(searchButton, gc);

        gc.gridx = 1;
        gc.gridy = 4;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 30, 50, 30);
        add(resultLabel, gc);

        gc.gridx = 1;
        gc.gridy = 5;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(0, 30, 0, 5);
        add(scrapeButton, gc);

        gc.gridx = 2;
        gc.gridy = 5;
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
                String result = controller.processUserRequest(url);
                resultLabel.setText(result);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            resultLabel.setText("Invalid URL! Please try again.");
        }
    }
}
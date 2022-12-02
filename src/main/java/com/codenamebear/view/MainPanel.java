package com.codenamebear.view;

import com.codenamebear.controller.Graph;
import com.codenamebear.controller.Scraper;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainPanel extends JPanel {

    // Components
    private final JList<String> resultsList;
    private final JList<String> startingList;
    private final JList<String> destinationList;
    private final JScrollPane resultsScroll;
    private final JLabel startingTopicLabel;
    private final JLabel destinationTopicLabel;
    private final JScrollPane startingScroll;
    private final JScrollPane destinationScroll;
    private final JButton searchButton;
    private final JButton scrapeButton;
    private final JLabel warningLabel;
    private final JLabel statsLabel;

    // Fonts
    private final static Font BOLD_FONT = new Font("San-Serif", Font.BOLD, 14);
    private final static Font PLAIN_FONT = new Font("San-Serif", Font.PLAIN, 14);

    // Layout
    private final GridBagConstraints gc = new GridBagConstraints();

    // Controllers
    private final Graph graph;
    private final Scraper scraper;

    /**
     * CONSTRUCTOR
     */
    public MainPanel(Graph graph, Scraper scraper, int panelWidth, int panelHeight) {

        this.graph = graph;
        this.scraper = scraper;

        // Set dimensions
        Dimension dim = getPreferredSize();
        dim.width = panelWidth;
        dim.height = panelHeight;
        setPreferredSize(dim);

        // Establish scroll panes with topic lists for starting and destination topics
        ArrayList<String> topics = new ArrayList<>(graph.getTopics());
        Collections.sort(topics);
        DefaultListModel<String> topicsList = new DefaultListModel<>();
        topicsList.addAll(topics);
        Dimension listDimensions = new Dimension(350, 400);

        this.startingList = new JList<>();
        this.startingList.setModel(topicsList);
        this.startingList.setFont(PLAIN_FONT);
        this.startingScroll = new JScrollPane(this.startingList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.startingScroll.setPreferredSize(listDimensions);
        this.startingScroll.setBorder(BorderFactory.createEtchedBorder());

        this.destinationList = new JList<>();
        this.destinationList.setModel(topicsList);
        this.destinationList.setFont(PLAIN_FONT);
        this.destinationScroll = new JScrollPane(this.destinationList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.destinationScroll.setPreferredSize(listDimensions);
        this.destinationScroll.setBorder(BorderFactory.createEtchedBorder());

        // Establish scroll pane with results list
        String[] initialResultText = {"Path will appear here, in order from top to bottom."};
        this.resultsList = new JList<>();
        this.resultsList.setListData(initialResultText);
        this.resultsList.setFont(PLAIN_FONT);
        this.resultsScroll = new JScrollPane(this.resultsList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.resultsScroll.setPreferredSize(new Dimension(500, 300));
        this.resultsScroll.setBorder(BorderFactory.createEtchedBorder());

        // Establish statistics label with topic count and disjoint sets count
        int seedsCount = this.graph.getGraph().size();
        int topicsCount = graph.getTopics().size();
        int disjointSetCount = this.graph.getDisjointSetCount();
        this.statsLabel = new JLabel("Seeds: " + seedsCount + "       Total number of topics: " + topicsCount +
                "       Disjoint sets: " + disjointSetCount);
        this.statsLabel.setFont(BOLD_FONT);

        // Initialize labels and buttons, and set font
        this.startingTopicLabel = new JLabel("Select starting topic:");
        this.startingTopicLabel.setFont(BOLD_FONT);

        this.destinationTopicLabel = new JLabel("Select destination topic:");
        this.destinationTopicLabel.setFont(BOLD_FONT);

        this.searchButton = new JButton("Find Shortest Path");
        this.searchButton.setFont(BOLD_FONT);

        this.scrapeButton = new JButton("Re-Scrape Content");
        this.scrapeButton.setFont(BOLD_FONT);

        this.warningLabel = new JLabel("WARNING: This may take several minutes!");
        this.warningLabel.setFont(BOLD_FONT);

        // Establish button action listeners
        this.searchButton.addActionListener(e ->
            getShortestPath()
        );

        this.scrapeButton.addActionListener(e -> {
            try {
                this.scraper.scrapeContent();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            warningLabel.setText("Scrape complete! Please reload the application.");
            warningLabel.setForeground(Color.red);
        });

        // Finalize panel appearance
        Border innerBorder = BorderFactory.createBevelBorder(1);
        Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        setLayout(new GridBagLayout());
        addComponents();
    }

    /**
     * GET THE SHORTEST PATH FROM THE USER-SELECTED STARTING TOPIC TO THE USER-SELECTED DESTINATION TOPIC
     * ASSIGN THE PATH DATA TO resultsList
     * DISPLAY THE resultsList DATA WITH resultsScroll
     */
    private void getShortestPath(){

        resultsScroll.setVisible(true);

        // Retrieve the user-selected starting topic and destination topic
        String startingTopic = this.startingList.getSelectedValue();
        String destinationTopic = this.destinationList.getSelectedValue();

        // Get the shortest path between the topics
        ArrayList<String> path = this.graph.getShortestPath(startingTopic, destinationTopic);

        // If the path is empty, assign an explanatory string to the results list
        // Otherwise, assign the path to the results list
        if(path.isEmpty()){
            String[] result = {"No path can be established; the source " +
                    "and destination belong to disjoint sets."};
            this.resultsList.setListData(result);
        } else{
            this.resultsList.setListData(path.toArray(new String[0]));
        }
    }

    /**
     * ADD ALL COMPONENTS TO THE PANEL
     */
    private void addComponents(){
        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = .5;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.SOUTH;
        add(this.startingTopicLabel, gc);

        gc.gridx = 2;
        add(this.destinationTopicLabel, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.NORTH;
        gc.insets = new Insets(30,0,0,0);
        add(this.startingScroll, gc);

        gc.gridx = 2;
        add(this.destinationScroll, gc);

        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = .2;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(20, 0, 0, 0);
        gc.anchor = GridBagConstraints.NORTH;
        add(this.searchButton, gc);

        gc.gridx = 1;
        gc.gridy = 6;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 2;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(20, 30, 0, 30);
        gc.anchor = GridBagConstraints.CENTER;
        add(this.resultsScroll, gc);

        gc.gridx = 1;
        gc.gridy = 7;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(20, 30, 0, 5);
        add(this.scrapeButton, gc);

        gc.gridx = 2;
        gc.gridy = 7;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(20, 5, 0, 30);
        add(this.warningLabel, gc);

        gc.gridx = 1;
        gc.gridy = 8;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = .2;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(20, 30, 30, 0);
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(this.statsLabel, gc);
    }
}
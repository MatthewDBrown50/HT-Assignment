package com.codenamebear.view;

import com.codenamebear.controller.Controller;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{

    private final JSplitPane splitPane;
    private final JPanel mainPanel;
    private final JPanel outputPanel;
    private final JScrollPane scroll;
    private final JLabel output; // This should eventually be changed to a JList<String>


    public MainFrame(Controller controller){
        super("Related URL Traverser");

        //Instantiate panels and split Pane
        splitPane = new JSplitPane();
        mainPanel = new MainPanel(controller);
        outputPanel = new JPanel();  // Replace this with a JList and figure out vertical scrolling


        //Set size of the window, create a grid layout and split the frame into two panels
        setSize(900, 975);
        getContentPane().setLayout(new GridLayout());
        getContentPane().add(splitPane);

        //Split pane options
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setTopComponent(mainPanel);
        splitPane.setBottomComponent(outputPanel);

        //Set up bottom panel output and scroll
        output = new JLabel();
        //Test text
        output.setText("<html> https://en.wikipedia.org/wiki/2022_Russian_invasion_of_Ukraine           <br>" +
                "https://en.wikipedia.org/wiki/2022_Russian_invasion_of_Ukraine</html>");

        //Set scroll bar to always vertical
        scroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setPreferredSize(new Dimension(850, 600));
        outputPanel.add(scroll);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }
}

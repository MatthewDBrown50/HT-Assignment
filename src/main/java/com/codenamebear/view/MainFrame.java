//==========================================
//  AUTHOR:    Matthew D Brown
//==========================================

package com.codenamebear.view;

import com.codenamebear.controller.Controller;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{

    public MainFrame(Controller controller){
        super("Related URL Finder");

        JPanel mainPanel = new MainPanel(controller);

        setLayout(new BorderLayout());

        add(mainPanel, BorderLayout.CENTER);

        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

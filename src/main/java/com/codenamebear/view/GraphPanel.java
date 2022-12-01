package com.codenamebear.view;

import com.codenamebear.controller.Controller;
import com.codenamebear.view.graphics.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Arrays;

public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {

   private GraphicMethods graphMethods;

    //initialises the frame and opens it
    public GraphPanel()
    {
            JButton open = new JButton("New Window");
            open.addActionListener(this);
            add(open);
            setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
            //code for the new frame
    }




    


}

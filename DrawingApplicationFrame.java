/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
/**
 * Creates a 2D drawing application in Java
 * @author kshitij
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private JPanel line1, line2, topPanel;
    // create the widgets for the firstLine Panel.
    private final JButton undo, clear;
    private final JLabel shape;
    private final JComboBox<String> shapeSelector;
    private final JCheckBox filled;
    //create the widgets for the secondLine Panel.
    private final JCheckBox useGradient, dashed;
    private final JButton firstColor, secondColor;
    private final JLabel lineWidth, dashLength;
    private final JTextField lineWidthInput, dashLengthInput;
    // Variables for drawPanel.
    private DrawPanel drawPanel;
    private Color color1, color2;
    private MyShapes currentShape;
    private final ArrayList<MyShapes> shapes;
    // add status label
    private JLabel statusLabel;
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        super("Java 2D Drawings");
        line1 = new JPanel();
        line2 = new JPanel();
        drawPanel = new DrawPanel();
        statusLabel = new JLabel("(0, 0)");
        topPanel = new JPanel();
        color1 = Color.BLACK;
        color2 = Color.WHITE;
        // add widgets to panels
        undo = new JButton("Undo");
        clear = new JButton("Clear");
        shape = new JLabel("Shape:");
        String[] sh = {"Line", "Rectangle", "Oval"};
        shapeSelector = new JComboBox<String>(sh);
        shapeSelector.setMaximumRowCount(3);
        filled = new JCheckBox("Filled");
        // firstLine widgets
        line1.add(undo);
        line1.add(clear);
        line1.add(shape);
        line1.add(shapeSelector);
        line1.add(filled);
        // secondLine widgets
        useGradient = new JCheckBox("Use Gradient");
        firstColor = new JButton("1st Color...");
        secondColor = new JButton("2nd Color...");
        lineWidth = new JLabel("Line Width:");
        dashLength = new JLabel("Dash Length:");
        lineWidthInput = new JTextField("10",2);
        dashLengthInput = new JTextField("15",2);
        dashed = new JCheckBox("Dashed");
        line2.add(useGradient);
        line2.add(firstColor);
        line2.add(secondColor);
        line2.add(lineWidth);
        line2.add(lineWidthInput);
        line2.add(dashLength);
        line2.add(dashLengthInput);
        line2.add(dashed);
        shapes = new ArrayList<>();
        // add top panel of two panels
        topPanel.setLayout(new GridLayout(2,1));
        topPanel.add(line1);
        topPanel.add(line2);
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        //add listeners and event handlers
        ButtonHandler buttonHandle = new ButtonHandler();
        undo.addActionListener(buttonHandle);
        clear.addActionListener(buttonHandle);
        firstColor.addActionListener(buttonHandle);
        secondColor.addActionListener(buttonHandle);
    }

    // Create event handlers, if needed
    private class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(event.getSource() == clear)
            {
                shapes.clear();
                drawPanel.repaint();
            }
            else if(event.getSource() == undo)
            {
                shapes.remove(shapes.size()-1);
                drawPanel.repaint();
            }
            else if(event.getSource() == firstColor)
            {
                color1 = JColorChooser.showDialog(DrawingApplicationFrame.this, "Select Color 1", color1);
                if(color1 == null)
                    color1 = Color.BLACK;
            }
            else if(event.getSource() == secondColor)
            {
                color2 = JColorChooser.showDialog(DrawingApplicationFrame.this, "Select Color 2", color2);
                if(color2 == null)
                    color2 = Color.WHITE;
            }
        }
    }
    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        public DrawPanel()
        {
            setBackground(Color.WHITE);
            MouseHandler mouseHandle = new MouseHandler();
            addMouseListener(mouseHandle);
            addMouseMotionListener(mouseHandle);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            //loop through and draw each shape in the shapes arraylist
            for(MyShapes shape: shapes)
            {
                shape.draw(g2d);
                repaint();
            }
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            public void mousePressed(MouseEvent event)
            {
                try{
                    Float.parseFloat(dashLengthInput.getText());
                }
                catch(NumberFormatException ex){
                    dashLengthInput.setText("15");
                }
                try{
                    Float.parseFloat(lineWidthInput.getText());
                }
                catch(NumberFormatException ex){
                    lineWidthInput.setText("10");
                }
                float dashes[] = {Float.parseFloat(dashLengthInput.getText())};
                String[] sh = {"Line", "Rectangle", "Oval"};
                Paint paint = color1;
                BasicStroke stroke = new BasicStroke(Float.parseFloat(lineWidthInput.getText()));
                if(useGradient.isSelected())
                    paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
                if(dashed.isSelected())
                    stroke = new BasicStroke(Float.parseFloat(lineWidthInput.getText()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0);
                switch(sh[shapeSelector.getSelectedIndex()])
                {
                    case "Line":
                    {
                        shapes.add(new MyLine(event.getPoint(),event.getPoint(), paint, stroke));
                        break;
                    }
                    case "Rectangle":
                    {
                        shapes.add(new MyRectangle(event.getPoint(),event.getPoint(), paint, stroke, filled.isSelected()));
                        break;
                    }
                    case "Oval":
                    {
                        shapes.add(new MyOval(event.getPoint(),event.getPoint(), paint, stroke, filled.isSelected()));
                        break;
                    }
                }
            }

            public void mouseReleased(MouseEvent event)
            {
                repaint();
                statusLabel.setText(String.format("(%d, %d)",event.getX(),event.getY()));
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                currentShape = shapes.get(shapes.size()-1);
                currentShape.setEndPoint(event.getPoint());
                shapes.set(shapes.size()-1, currentShape);
                repaint();
                statusLabel.setText(String.format("(%d, %d)",event.getX(),event.getY()));
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                statusLabel.setText(String.format("(%d, %d)",event.getX(),event.getY()));
            }
        }

    }
}

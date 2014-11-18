package main.java.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jakub Fortunka on 12.11.14.
 */
public class SideOptionPanel extends JPanel {

    private JSpinner agentsNumber;

    public SideOptionPanel(String identifier) {
        setBorder(BorderFactory.createTitledBorder(identifier));
        //add(bluePanel);
        setLayout(new GridLayout(1, 0, 0, 0));

        JPanel agentsSliderPanel = new JPanel();
        agentsSliderPanel.setBorder(BorderFactory.createTitledBorder("No. Agents"));
        add(agentsSliderPanel);

        agentsNumber= new JSpinner();

        agentsSliderPanel.add(agentsNumber);

        JSlider agentsSlider = new JSlider();
        agentsSlider.setValue(20);
        agentsNumber.setValue(20);
        agentsSliderPanel.add(agentsSlider);

    }

    public int getAgentsNumber() {
        return (int) agentsNumber.getValue();
    }
}

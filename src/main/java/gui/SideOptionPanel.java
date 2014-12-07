package main.java.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by Jakub Fortunka on 12.11.14.
 */

public class SideOptionPanel extends JPanel {

    private JSpinner agentsNumber;
    private JSlider agentsSlider;

    public SideOptionPanel(String identifier) {
        setBorder(BorderFactory.createTitledBorder(identifier));
        //add(bluePanel);
        setLayout(new GridLayout(1, 0, 0, 0));

        JPanel agentsSliderPanel = new JPanel();
        agentsSliderPanel.setBorder(BorderFactory.createTitledBorder("No. Agents"));
        add(agentsSliderPanel);

        agentsNumber= new JSpinner();

        agentsSliderPanel.add(agentsNumber);

        agentsSlider = new JSlider();
        agentsSlider.setValue(20);
        
        agentsNumber.setValue(20);
        agentsSliderPanel.add(agentsSlider);
    }
    
    public void sliderMoved() {
    	agentsNumber.setValue(agentsSlider.getValue());
    }

    public void setSliderChangeListener(ChangeListener listener) {
        agentsSlider.addChangeListener(listener);
    }

    public JSpinner getAgentsNumberSpinner() {
        return agentsNumber;
    }

    public int getAgentsNumber() {
        return (int) agentsNumber.getValue();
    }
    
}

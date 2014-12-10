package main.java.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Created by Jakub Fortunka on 12.11.14.
 */

public class SideOptionPanel extends JPanel {

    private JSpinner warriorsNumber;
    private JSlider warriorsSlider;

    private JSpinner archersNumber;
    private JSlider archersSlider;

    public SideOptionPanel(String identifier) {
        setBorder(BorderFactory.createTitledBorder(identifier));
        //add(bluePanel);
        //setLayout(new GridLayout(0, 1, 0, 0));
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        JPanel warriorsSliderPanel = new JPanel();
        warriorsSliderPanel.setBorder(BorderFactory.createTitledBorder("No. of Warriors"));
        add(warriorsSliderPanel);

        warriorsNumber = new JSpinner();

        warriorsSliderPanel.add(warriorsNumber);

        warriorsSlider = new JSlider();
        warriorsSlider.setValue(10);
        
        warriorsNumber.setValue(10);
        warriorsSliderPanel.add(warriorsSlider);

        JPanel archersSliderPanel = new JPanel();
        archersSliderPanel.setBorder(BorderFactory.createTitledBorder("No. of Archers"));
        add(archersSliderPanel);

        archersNumber = new JSpinner();

        archersSliderPanel.add(archersNumber);

        archersSlider = new JSlider();
        archersSlider.setValue(10);

        archersNumber.setValue(10);
        archersSliderPanel.add(archersSlider);
    }
    
    public void sliderMoved() {
    	warriorsNumber.setValue(warriorsSlider.getValue());
        archersNumber.setValue(archersSlider.getValue());
    }

    public void setSliderChangeListener(ChangeListener listener) {
        warriorsSlider.addChangeListener(listener);
        archersSlider.addChangeListener(listener);
    }

    public JSpinner getAgentsNumberSpinner() {
        return warriorsNumber;
    }

    public int getWarriorsNumber() {
        return (int) warriorsNumber.getValue();
    }

    public int getArchersNumber() {
        return (int) archersNumber.getValue();
    }
    
}

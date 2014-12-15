package main.java.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Created by Jakub Fortunka on 12.11.14.
 *
 */

public class SideOptionPanel extends JPanel {
    private static final int HP = 0;
    private static final int STR = 1;
    private static final int SPD = 2;
    private static final int ACC = 3;
    public static final int ROA = 4;

    private static final String[] labels = {"condition", "strength", "speed", "accuracy", "range"};
    private static final int[] vals = {40, 6, 7, 90, 3};
    public final JSpinner[] options = new JSpinner[vals.length];
    private JSpinner warriorsNumber;
    private JSlider warriorsSlider;

    private JSpinner archersNumber;
    private JSlider archersSlider;

    private JSpinner commandersNumber;
    private JSlider commandersSlider;

    public SideOptionPanel(String identifier) {
        setBorder(BorderFactory.createTitledBorder(identifier));
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        JPanel warriorsSliderPanel = new JPanel();
        warriorsSliderPanel.setLayout(new BoxLayout(warriorsSliderPanel, BoxLayout.PAGE_AXIS));

        warriorsSliderPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.warriorsBorderTitle")));
        add(warriorsSliderPanel);

        warriorsNumber = new JSpinner(new SpinnerNumberModel(10, 0, 500, 1));

        warriorsSliderPanel.add(warriorsNumber);

        warriorsSlider = new JSlider();
        warriorsSlider.setValue(10);

        warriorsNumber.setValue(10);
        warriorsSliderPanel.add(warriorsSlider);

        JPanel row = new JPanel();
        row.add(warriorsSlider);
        row.add(warriorsNumber);
        warriorsSliderPanel.add(row);

        for (int i = 0; i < labels.length; i+=2) {
            row = new JPanel();
            for (int col = 0; col < 2; col++) {
                int j = i + col;
                if (j >= labels.length) break;

                JLabel l = new JLabel(labels[j], JLabel.TRAILING);
                options[j] = new JSpinner();
                options[j].setValue(vals[j]);

                row.add(l);
                row.add(options[j]);
            }
            warriorsSliderPanel.add(row);
        }

        JPanel archersSliderPanel = new JPanel();
        archersSliderPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.archersBorderTitle")));
        add(archersSliderPanel);

        archersNumber = new JSpinner(new SpinnerNumberModel(10, 0, 500, 1));

        archersSliderPanel.add(archersNumber);

        archersSlider = new JSlider();
        archersSlider.setValue(10);

        archersNumber.setValue(10);
        archersSliderPanel.add(archersSlider);

        JPanel commandersSliderPanel = new JPanel();
        commandersSliderPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.commandersBorderTitle")));
        add(commandersSliderPanel);

        commandersNumber = new JSpinner(new SpinnerNumberModel(1, 0, 500, 1));

        commandersSliderPanel.add(commandersNumber);

        commandersSlider = new JSlider();
        commandersSlider.setValue(1);

        commandersNumber.setValue(1);
        commandersSliderPanel.add(commandersSlider);
    }
    
    public void sliderMoved() {
    	warriorsNumber.setValue(warriorsSlider.getValue());
        archersNumber.setValue(archersSlider.getValue());
        commandersNumber.setValue(commandersSlider.getValue());
    }

    public void setSliderChangeListener(ChangeListener listener) {
        warriorsSlider.addChangeListener(listener);
        archersSlider.addChangeListener(listener);
        commandersSlider.addChangeListener(listener);
    }

    public int getWarriorsNumber() {
        return (int) warriorsNumber.getValue();
    }

    public int getArchersNumber() {
        return (int) archersNumber.getValue();
    }

    public int getCommandersNumber() { return (int) commandersNumber.getValue(); }

    public Integer getRange() {
        return (Integer) options[ROA].getValue();
    }

    public Integer getCondition() {
        return (Integer) options[HP].getValue();
    }

    public Integer getAccuracy() {
        return (Integer) options[ACC].getValue();
    }

    public Integer getStrength() {
        return (Integer) options[STR].getValue();
    }

    public Integer getSpeed() {
        return (Integer) options[SPD].getValue();
    }
}

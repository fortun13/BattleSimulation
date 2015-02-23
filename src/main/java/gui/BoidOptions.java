package main.java.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marek on 2014-12-16.
 * Awesome panel with boids options
 */
public class BoidOptions extends JPanel {
    BoidOptions() {
        setBorder(BorderFactory.createTitledBorder(Messages.getString("BoidOptions.parametersBorderName")));
        GridLayout gridLayout = new GridLayout((int) Math.ceil(BoidOptions.labels.length / 2.), 4);
        setLayout(gridLayout);

        for (int i = 0; i < BoidOptions.labels.length; i++) {
            options[i] = new JSpinner();
            options[i].setValue(vals[i]);
            options[i].setMaximumSize(new Dimension(300,100));
            add(new JLabel(BoidOptions.labels[i]));

            // dzięki temu zabiegowi slidery nie są rozciągnięte na cały obszar komórki 'GridLayout'
            JPanel beautifuler = new JPanel(new GridBagLayout());
            beautifuler.add(options[i], new GridBagConstraints());
            add(beautifuler);
        }
    }

    private static final String[] labels = {
            Messages.getString("BoidOptions.lblAS.text"),
            Messages.getString("BoidOptions.lblROV.text"),
            Messages.getString("BoidOptions.lblAOV.text"),
            Messages.getString("BoidOptions.lblT.text"),
            Messages.getString("BoidOptions.lblFW.text"),
            Messages.getString("BoidOptions.lblSCW.text"),
            Messages.getString("BoidOptions.lblAW.text"),
            Messages.getString("BoidOptions.lblMD.text")};
    private static final int[] vals = {20, 300, 120, 80, 10, 10, 80, 40};
    private static final int AS = 0, AOV = 1,ROV = 2,T = 3,FW = 4,SCW = 5,AW = 6,MD = 7;
    private final JSpinner[] options = new JSpinner[BoidOptions.labels.length];

    public double getAgentSize() {
        return (Integer) options[AS].getValue();
    }
    public double getAngleOfView() {
        return (Integer) options[AOV].getValue();
    }
    public double getRangeOfView() {
        return (Integer) options[ROV].getValue();
    }
    public double getTemporize() {
        return (Integer) options[T].getValue();
    }
    public double getFollowingWeight() {
        return (Integer) options[FW].getValue() / 100.;
    }
    public double getSeekCenterWeight() {
        return (Integer) options[SCW].getValue() / 100.;
    }
    public double getAvoidingWeight() {
        return (Integer) options[AW].getValue() / 100.;
    }
    public double getMinimalDistance() {
        return (Integer) options[MD].getValue();
    }

    public void setAgentSize(double val) {
        options[AS].setValue(val);
    }
    public void setAngleOfView(double val) {
        options[AOV].setValue(val);
    }
    public void setRangeOfView(double val) {
        options[ROV].setValue(val);
    }
    public void setTemporize(double val) {
        options[T].setValue(val);
    }
    public void setFollowingWeight(double val) {
        options[FW].setValue(val);
    }
    public void setSeekCenterWeight(double val) {
        options[SCW].setValue(val);
    }
    public void setAvoidingWeight(double val) {
        options[AW].setValue(val);
    }
    public void setMinimalDistance(double val) {
        options[MD].setValue(val);
    }

}

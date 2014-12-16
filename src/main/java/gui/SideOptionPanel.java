package main.java.gui;

import main.java.agents.World;
import main.java.agents.World.AgentType;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;

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
    private static final int ATRACTIONFORCE = 5;

	private static final String[] labels = {Messages.getString("SideOptionPanel.parametersConditionLabel"),
		Messages.getString("SideOptionPanel.parametersStrengthLabel"),
		Messages.getString("SideOptionPanel.parametersSpeedLabel"),
		Messages.getString("SideOptionPanel.parametersAccuracyLabel"),
		Messages.getString("SideOptionPanel.parametersRangeLabel")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	HashMap<World.AgentType,JSpinner[]> optionsPerType = new HashMap<>();

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

		warriorsSliderPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.warriorsBorderTitle"))); //$NON-NLS-1$
		add(warriorsSliderPanel);
		warriorsSliderPanel.setLayout(new BoxLayout(warriorsSliderPanel, BoxLayout.Y_AXIS));

		JPanel warriorsNumberPanel = new JPanel();
		warriorsSliderPanel.add(warriorsNumberPanel);
		//warriorsSliderPanel.setLayout(new BoxLayout(warriorsSliderPanel, BoxLayout.Y_AXIS));

		warriorsNumber = new JSpinner(new SpinnerNumberModel(10, 0, 500, 1));
		warriorsNumberPanel.add(warriorsNumber);

		warriorsNumber.setValue(10);

		warriorsSlider = new JSlider();
		warriorsNumberPanel.add(warriorsSlider);
		warriorsSlider.setValue(10);

		createParametersPanel(warriorsSliderPanel, World.AgentType.WARRIOR, new int[] {160, 6, 7, 90, 23});

		JPanel archersSliderPanel = new JPanel();
		archersSliderPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.archersBorderTitle"))); //$NON-NLS-1$
		add(archersSliderPanel);


		archersSliderPanel.setLayout(new BoxLayout(archersSliderPanel, BoxLayout.Y_AXIS));

		JPanel archersNumberPanel = new JPanel();
		archersSliderPanel.add(archersNumberPanel);

		archersNumber = new JSpinner(new SpinnerNumberModel(10, 0, 500, 1));
		archersNumberPanel.add(archersNumber);

		archersNumber.setValue(10);

		archersSlider = new JSlider();
		archersNumberPanel.add(archersSlider);
		archersSlider.setValue(10);

		createParametersPanel(archersSliderPanel, World.AgentType.ARCHER, new int[]{10, 3, 4, 95, 140});

		JPanel commandersSliderPanel = new JPanel();
		commandersSliderPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.commandersBorderTitle"))); //$NON-NLS-1$
		add(commandersSliderPanel);
		commandersSliderPanel.setLayout(new BoxLayout(commandersSliderPanel, BoxLayout.Y_AXIS));

		JPanel commandersNumberPanel = new JPanel();
		commandersSliderPanel.add(commandersNumberPanel);

		commandersNumber = new JSpinner(new SpinnerNumberModel(1, 0, 500, 1));
		commandersNumberPanel.add(commandersNumber);

		commandersNumber.setValue(1);

		commandersSlider = new JSlider();
		commandersNumberPanel.add(commandersSlider);
		commandersSlider.setValue(1);

		createParametersPanel(commandersSliderPanel, AgentType.COMMANDER, new int[] {60,10,7,95,3});

	}

	private void createParametersPanel(JPanel panelToAdd, World.AgentType type, int[] defaultValues) {
        JSpinner[] options;
        switch (type) {
            case COMMANDER:
                options = new JSpinner[defaultValues.length+1];
                break;
            default:
                options = new JSpinner[defaultValues.length];
        }
		JPanel parameters = new JPanel();
		parameters.setBorder(BorderFactory.createTitledBorder(Messages.getString("SideOptionPanel.parametersBordername"))); //$NON-NLS-1$
		panelToAdd.add(parameters);
		parameters.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel row = new JPanel();

		for (int i = 0; i < labels.length; i+=2) {
			row = new JPanel();
			row.setLayout(new FlowLayout());
			for (int col = 0; col < 2; col++) {
				int j = i + col;
				if (j >= labels.length) break;

				JLabel l = new JLabel(labels[j], JLabel.TRAILING);
				options[j] = new JSpinner();
				options[j].setValue(defaultValues[j]);

				row.add(l);
				row.add(options[j]);
			}
			parameters.add(row);
		}

        if (type == AgentType.COMMANDER) {
            JLabel l = new JLabel(Messages.getString("sideOptionPanel.commanderAttractionForceLabel"), JLabel.TRAILING);
            options[labels.length] = new JSpinner();
            options[labels.length].setValue(100);
            row.add(l);
            row.add(options[labels.length]);
        }
		optionsPerType.put(type,options);
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

	public Integer getRange(World.AgentType type) {
		return (Integer) optionsPerType.get(type)[ROA].getValue();
	}

	public Integer getCondition(World.AgentType type) {
		return (Integer) optionsPerType.get(type)[HP].getValue();
	}

	public Integer getAccuracy(World.AgentType type) {
		return (Integer) optionsPerType.get(type)[ACC].getValue();
	}

	public Integer getStrength(World.AgentType type) {
		return (Integer) optionsPerType.get(type)[STR].getValue();
	}

	public Integer getSpeed(World.AgentType type) {
		return (Integer) optionsPerType.get(type)[SPD].getValue();
	}

    public Integer getAttractionForce() {
        return (Integer) optionsPerType.get(AgentType.COMMANDER)[ATRACTIONFORCE].getValue();
    }
}

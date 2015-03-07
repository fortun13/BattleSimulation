package gui;

import agents.AgentType;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OptionsPanel extends JPanel {

	private final JButton btnSaveToFile;
	public final BoidOptions boidOptions = new BoidOptions();
	private JSpinner timeStepSpinner;
	private JSpinner turnsLimitSpinner;
	private JCheckBox limitButton;
	private JSpinner boardWidth;
	private JSpinner boardHeight;

	private JFileChooser fileChooser = new JFileChooser();
	private JButton openFile;

    public SideOptionsPanel bluePanel;
    public SideOptionsPanel redPanel;

    public OptionsPanel() {

        bluePanel = new SideOptionsPanel(Messages.getString("OptionsPanel.bluePanelBorderTitle"));
        redPanel = new SideOptionsPanel(Messages.getString("OptionsPanel.redPanelBorderTitle"));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(bluePanel);
        add(redPanel);
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("OptionsPanel.generalBorderTitle")));
		add(generalPanel);
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
		//generalPanel.setLayout(new BoxLayout(generalPanel,BoxLayout.Y_AXIS));
		
		JPanel generalBoardSize = new JPanel();
		generalBoardSize.setBorder(BorderFactory.createTitledBorder(Messages.getString("OptionsPanel.boardSizeBorderTitle")));
		generalPanel.add(generalBoardSize);
		generalBoardSize.setLayout(new BoxLayout(generalBoardSize, BoxLayout.Y_AXIS));
		
		JPanel boardWidthPanel = new JPanel();
		generalBoardSize.add(boardWidthPanel);
		
		JLabel lblWidth = new JLabel(Messages.getString("OptionsPanel.lblWidth.text")); //$NON-NLS-1$
		boardWidthPanel.add(lblWidth);
		
		boardWidth = new JSpinner();
		boardWidth.setValue(50);
		boardWidthPanel.add(boardWidth);
		
		JPanel boardHeightPanel = new JPanel();
		generalBoardSize.add(boardHeightPanel);
		
		JLabel lblHeight = new JLabel(Messages.getString("OptionsPanel.lblHeight.text")); //$NON-NLS-1$
		boardHeightPanel.add(lblHeight);
		
		boardHeight = new JSpinner();
		boardHeight.setValue(50);
		boardHeightPanel.add(boardHeight);

		JPanel timeOptionsPanel = new JPanel();
		timeOptionsPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("OptionsPanel.timeOptionsBorderTitle")));
		timeOptionsPanel.setLayout(new BoxLayout(timeOptionsPanel, BoxLayout.Y_AXIS));
		
		JPanel timeStepP = new JPanel();
		JLabel lblTimeStep = new JLabel(Messages.getString("OptionsPanel.timeStepLabelHeight.text"));
		timeStepP.add(lblTimeStep);
		timeStepSpinner = new JSpinner(new SpinnerNumberModel(40, 1, 1000, 1));
		timeStepP.add(timeStepSpinner);
		timeOptionsPanel.add(timeStepP);

		timeOptionsPanel.add(boidOptions);

		JPanel turnsLimitPanel = new JPanel();
		JLabel lblTurnsLimit = new JLabel(Messages.getString("OptionsPanel.turnsLimit.text"));
		turnsLimitPanel.add(lblTurnsLimit);
		turnsLimitSpinner = new JSpinner(new SpinnerNumberModel(500, 100, 10000, 1));
		turnsLimitPanel.add(turnsLimitSpinner);
		limitButton = new JCheckBox(Messages.getString("OptionsPanel.turnsLimitCB.text"));
		limitButton.setSelected(false);
		turnsLimitPanel.add(limitButton);
		timeOptionsPanel.add(turnsLimitPanel);

		generalPanel.add(timeOptionsPanel);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));

		openFile = new JButton(Messages.getString("OptionsPanel.openFile.text")); //$NON-NLS-1$
		buttons.add(openFile);
		openFile.setAlignmentX(Component.CENTER_ALIGNMENT);

		buttons.add(Box.createRigidArea(new Dimension(0,10)));
        generalPanel.add(buttons);
        
        btnSaveToFile = new JButton(Messages.getString("OptionsPanel.btnSaveToFile.text")); //$NON-NLS-1$
        buttons.add(btnSaveToFile);
		btnSaveToFile.setAlignmentX(Component.CENTER_ALIGNMENT);
	}

	public void openFileAddActionListener(ActionListener listener) {
		openFile.addActionListener(listener);
	}

	public void saveToFileAddActionListener(ActionListener listener) {
		btnSaveToFile.addActionListener(listener);
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Method returns board size set by user
	 *
	 * @return Pair of (Height,Width)
	 */
	public Pair<Integer, Integer> getBoardSize() {
		return new Pair<>((Integer)boardHeight.getValue(),(Integer)boardWidth.getValue());
	}

    /**
     * @return number of turns to which simulation is limited
     */
    public int getTurnsLimit() {
        return (int)turnsLimitSpinner.getValue();
    }

	private ArrayList<Pair<AgentType,Integer>> getListWithNumberOfAgentsByType(SideOptionsPanel panel) {
		ArrayList<Pair<AgentType,Integer>> list = new ArrayList<>();
		list.add(new Pair<>(AgentType.WARRIOR,panel.getWarriorsNumber()));
		list.add(new Pair<>(AgentType.ARCHER,panel.getArchersNumber()));
		list.add(new Pair<>(AgentType.COMMANDER,panel.getCommandersNumber()));
		return list;
	}

	public ArrayList<Pair<AgentType,Integer>> getBluesAgents() {
		return getListWithNumberOfAgentsByType(bluePanel);
	}

	public ArrayList<Pair<AgentType,Integer>> getRedsAgents() {
		return getListWithNumberOfAgentsByType(redPanel);
	}

    public void setSidePanelsSliderListener(ChangeListener listener) {
        redPanel.setSliderChangeListener(listener);
        bluePanel.setSliderChangeListener(listener);
    }

    /**
     * @return size of timestep (in milliseconds)
     */
	public int getTimeStep() {
		return (int)timeStepSpinner.getValue();
	}

	public void setBoardSize(int boardWidth, int boardHeight) {
		this.boardHeight.setValue(boardHeight);
		this.boardWidth.setValue(boardWidth);
	}

    /**
     * @return true if user choosed to limit number of turns
     */
    public boolean limitIsActive() {
        return limitButton.isSelected();
    }

    public void setTimestep(int val) {
        timeStepSpinner.setValue(val);
    }

    public void setLimitCheckbox(boolean b) {
        limitButton.setSelected(b);
    }

    public void setTurnNumber(int val) {
        timeStepSpinner.setValue(val);
    }
}

package main.java.gui;

import javafx.util.Pair;
import main.java.agents.World;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OptionsPanel extends JPanel {

	private final JButton btnSaveToFile;
	private JSpinner timeStepSpinner;
	public JSpinner as;
	public JSpinner aov;
	public JSpinner rov;
	public JSpinner t;
	public JSpinner dosw;
	public JSpinner comw;
	public JSpinner aw;
	public JSpinner mind;
	private JSpinner boardWidth;
	private JSpinner boardHeight;

	private JFileChooser fileChooser = new JFileChooser();
	private JButton openFile;

    public SideOptionPanel bluePanel;
    public SideOptionPanel redPanel;

    public OptionsPanel() {
		setLayout(new GridLayout(1, 0, 0, 0));

        bluePanel = new SideOptionPanel(Messages.getString("OptionsPanel.bluePanelBorderTitle"));
        redPanel = new SideOptionPanel(Messages.getString("OptionsPanel.redPanelBorderTitle"));

        add(bluePanel);
        add(redPanel);
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("OptionsPanel.generalBorderTitle")));
		add(generalPanel);
		generalPanel.setLayout(new GridLayout(0, 1, 5, 5));
		//generalPanel.setLayout(new BoxLayout(generalPanel,BoxLayout.Y_AXIS));
		
		JPanel generalBoardSize = new JPanel();
		generalBoardSize.setBorder(BorderFactory.createTitledBorder(Messages.getString("OptionsPanel.boardSizeBorderTitle")));
		generalPanel.add(generalBoardSize);
		generalBoardSize.setLayout(new GridLayout(1, 2, 0, 0));
		
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

		JPanel timeStepPanel = new JPanel();
		timeStepPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("OptionsPanel.timeStepBorderTitle")));
		timeStepSpinner = new JSpinner();
		timeStepSpinner.setValue(40);
		timeStepPanel.add(timeStepSpinner);
		as = new JSpinner();
		as.setValue(10);
		timeStepPanel.add(as);

		generalPanel.add(timeStepPanel);

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

	private ArrayList<Pair<World.AgentType,Integer>> getListWithNumberOfAgentsByType(SideOptionPanel panel) {
		ArrayList<Pair<World.AgentType,Integer>> list = new ArrayList<>();
		list.add(new Pair<>(World.AgentType.WARRIOR,panel.getWarriorsNumber()));
		list.add(new Pair<>(World.AgentType.ARCHER,panel.getArchersNumber()));
		list.add(new Pair<>(World.AgentType.COMMANDER,panel.getCommandersNumber()));
		return list;
	}

	public ArrayList<Pair<World.AgentType,Integer>> getBluesAgents() {
		return getListWithNumberOfAgentsByType(bluePanel);
	}

	public ArrayList<Pair<World.AgentType,Integer>> getRedsAgents() {
		return getListWithNumberOfAgentsByType(redPanel);
	}

    public void setSidePanelsSliderListener(ChangeListener listener) {
        redPanel.setSliderChangeListener(listener);
        bluePanel.setSliderChangeListener(listener);
    }

	public int getTimeStep() {
		return (int)timeStepSpinner.getValue();
	}

    public JSpinner getBoardWidth() {
        return boardWidth;
    }

	public void setBoardSize(int boardWidth, int boardHeight) {
		this.boardHeight.setValue(boardHeight);
		this.boardWidth.setValue(boardWidth);
	}
}

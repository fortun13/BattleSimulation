package main.java.gui;

import javafx.util.Pair;
import main.java.agents.World;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OptionsPanel extends JPanel {
	
	private JButton generateBoard;
    private JButton spawnAgents;
	//private JButton btnStartSimulation;
	private JSpinner boardWidth;
	private JSpinner boardHeight;

	private JFileChooser fileChooser = new JFileChooser();
	private JButton openFile;

    private SideOptionPanel bluePanel;
    private SideOptionPanel redPanel;

    public OptionsPanel() {
		setLayout(new GridLayout(1, 0, 0, 0));

        bluePanel = new SideOptionPanel("Blues");
        redPanel = new SideOptionPanel("Reds");

        add(bluePanel);
        add(redPanel);
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
		add(generalPanel);
		generalPanel.setLayout(new GridLayout(0, 1, 5, 5));
		//generalPanel.setLayout(new BoxLayout(generalPanel,BoxLayout.Y_AXIS));
		
		JPanel generalBoardSize = new JPanel();
		generalBoardSize.setBorder(BorderFactory.createTitledBorder("Board size"));
		generalPanel.add(generalBoardSize);
		generalBoardSize.setLayout(new GridLayout(1, 2, 0, 0));
		
		JPanel boardWidthPanel = new JPanel();
		generalBoardSize.add(boardWidthPanel);
		
		JLabel lblWidth = new JLabel("Width");
		boardWidthPanel.add(lblWidth);
		
		boardWidth = new JSpinner();
		boardWidth.setValue(50);
		boardWidthPanel.add(boardWidth);
		
		JPanel boardHeightPanel = new JPanel();
		generalBoardSize.add(boardHeightPanel);
		
		JLabel lblHeight = new JLabel("Height");
		boardHeightPanel.add(lblHeight);
		
		boardHeight = new JSpinner();
		boardHeight.setValue(50);
		boardHeightPanel.add(boardHeight);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));

		generateBoard = new JButton("Generate Board");
		buttons.add(generateBoard);
		generateBoard.setAlignmentX(Component.CENTER_ALIGNMENT);

		buttons.add(Box.createRigidArea(new Dimension(0,10)));

		openFile = new JButton("Choose File");
		buttons.add(openFile);
		openFile.setAlignmentX(Component.CENTER_ALIGNMENT);

		buttons.add(Box.createRigidArea(new Dimension(0,10)));

        spawnAgents = new JButton("Spawn Agents");
		spawnAgents.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttons.add(spawnAgents);
        //test.add(btnStartSimulation);
        generalPanel.add(buttons);
	}
	
	public void generateButtonAddActionListener(ActionListener listener) {
		generateBoard.addActionListener(listener);
	}

	public void openFileAddActionListener(ActionListener listener) {
		openFile.addActionListener(listener);
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

    public void spawnAgentsAddActionListener(ActionListener listener) {
        spawnAgents.addActionListener(listener);
    }
	
	/*public void startSimulationButtonAddActionListener(ActionListener listener) {
		btnStartSimulation.addActionListener(listener);
	}*/
	
	public Pair<Integer, Integer> getBoardSize() {
		return new Pair<>((Integer)boardHeight.getValue(),(Integer)boardWidth.getValue());
	}
	
	public int getBluesAgentsNumber() {
		return bluePanel.getWarriorsNumber();
	}

	private ArrayList<Pair<World.AgentType,Integer>> getListWithNumberOfAgentsByType(SideOptionPanel panel) {
		ArrayList<Pair<World.AgentType,Integer>> list = new ArrayList<>();
		list.add(new Pair<>(World.AgentType.WARRIOR,panel.getWarriorsNumber()));
		list.add(new Pair<>(World.AgentType.ARCHER,panel.getArchersNumber()));
		return list;
	}

	public ArrayList<Pair<World.AgentType,Integer>> getBluesAgents() {
		return getListWithNumberOfAgentsByType(bluePanel);
	}

	public ArrayList<Pair<World.AgentType,Integer>> getRedsAgents() {
		return getListWithNumberOfAgentsByType(redPanel);
	}
	
	public int getRedsAgentsNumber() {
		return redPanel.getWarriorsNumber();
	}

    public void setSidePanelsSliderListener(ChangeListener listener) {
        redPanel.setSliderChangeListener(listener);
        bluePanel.setSliderChangeListener(listener);
    }

    public JSpinner getBoardWidth() {
        return boardWidth;
    }
}

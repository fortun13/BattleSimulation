package main.java.gui;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class OptionsPanel extends JPanel {
	
	private JButton generateBoard;
    private JButton spawnAgents;
	private JButton btnStartSimulation;
	private JSpinner boardWidth;
	private JSpinner boardHeight;

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
		generalPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel generalBoardSize = new JPanel();
		generalBoardSize.setBorder(BorderFactory.createTitledBorder("Board size"));
		generalPanel.add(generalBoardSize);
		generalBoardSize.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel boardWidthPanel = new JPanel();
		generalBoardSize.add(boardWidthPanel);
		
		JLabel lblWidth = new JLabel("Width");
		boardWidthPanel.add(lblWidth);
		
		boardWidth = new JSpinner();
		boardWidth.setValue(20);
		boardWidthPanel.add(boardWidth);
		
		JPanel boardHeightPanel = new JPanel();
		generalBoardSize.add(boardHeightPanel);
		
		JLabel lblHeight = new JLabel("Height");
		boardHeightPanel.add(lblHeight);
		
		boardHeight = new JSpinner();
		boardHeight.setValue(20);
		boardHeightPanel.add(boardHeight);

		generateBoard = new JButton("Generate Board");
		generalPanel.add(generateBoard);

        spawnAgents = new JButton("Spawn Agents");
        btnStartSimulation = new JButton("Start simulation");

        JPanel test = new JPanel();
        test.add(spawnAgents);
        test.add(btnStartSimulation);
        generalPanel.add(test);
	}
	
	public void generateButtonAddActionListener(ActionListener listener) {
		generateBoard.addActionListener(listener);
	}

    public void spawnAgentsAddActionListener(ActionListener listener) {
        spawnAgents.addActionListener(listener);
    }
	
	public void startSimulationButtonAddActionListener(ActionListener listener) {
		btnStartSimulation.addActionListener(listener);
	}
	
	public Pair<Integer, Integer> getBoardSize() {
		return new Pair<>((Integer)boardHeight.getValue(),(Integer)boardWidth.getValue());
	}
	
	public int getBluesAgentsNumber() {
		return bluePanel.getAgentsNumber();
	}
	
	public int getRedsAgentsNumber() {
		return redPanel.getAgentsNumber();
	}

    public void setSidePanelsSliderListener(ChangeListener listener) {
        redPanel.setSliderChangeListener(listener);
        bluePanel.setSliderChangeListener(listener);
    }

    public JSpinner getBoardWidth() {
        return boardWidth;
    }
}

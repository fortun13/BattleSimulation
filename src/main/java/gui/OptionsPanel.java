package main.java.gui;

import java.awt.Dimension;

import javafx.util.Pair;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.JSpinner;
import javax.swing.JButton;

public class OptionsPanel extends JPanel {
	
	//private final int WIDTH = 800;
	//private final int HEIGTH = 300;
	
	private JButton generateBoard;
	private JSpinner boardWidth;
	private JSpinner boardHeight;
	
	public OptionsPanel() {
		setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel bluePanel = new JPanel();
		bluePanel.setBorder(BorderFactory.createTitledBorder("Blues"));
		add(bluePanel);
		bluePanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel blueAgentsSliderPanel = new JPanel();
		blueAgentsSliderPanel.setBorder(BorderFactory.createTitledBorder("No. Agents"));
		bluePanel.add(blueAgentsSliderPanel);
		
		JSpinner bluesAgentsSpinner = new JSpinner();
		
		blueAgentsSliderPanel.add(bluesAgentsSpinner);
		
		JSlider blueAgentsSlider = new JSlider();
		blueAgentsSlider.setValue(20);
		bluesAgentsSpinner.setValue(new Integer(20));
		blueAgentsSliderPanel.add(blueAgentsSlider);
		
		JPanel redPanel = new JPanel();
		redPanel.setBorder(BorderFactory.createTitledBorder("Reds"));
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
		boardWidth.setValue(new Integer(20));
		boardWidthPanel.add(boardWidth);
		
		JPanel boardHeightPanel = new JPanel();
		generalBoardSize.add(boardHeightPanel);
		
		JLabel lblHeight = new JLabel("Height");
		boardHeightPanel.add(lblHeight);
		
		boardHeight = new JSpinner();
		boardHeight.setValue(new Integer(20));
		boardHeightPanel.add(boardHeight);
		
		generateBoard = new JButton("Generate Board");
		generalPanel.add(generateBoard);
		
		//setSize(new Dimension(WIDTH,HEIGHT));
	}
	
	public void generateButtonAddActionListener(ActionListener listener) {
		generateBoard.addActionListener(listener);
	}
	
	public Pair<Integer, Integer> getBoardSize() {
		return new Pair<Integer,Integer>(new Integer((int)boardHeight.getValue()),new Integer((int)boardWidth.getValue()));
	}

}

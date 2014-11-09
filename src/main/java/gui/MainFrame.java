package main.java.gui;

import main.java.agents.ServerAgent;

import javax.swing.*;

import java.awt.*;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class MainFrame extends JFrame {

    private final int FRAMEHEIGHT = 700;

	private final int FRAMEWIDTH = 800;

	private BoardPanel boardPanel;

    private ServerAgent server;

    public MainFrame(ServerAgent s) {

        setTitle("Battle!");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        setSize(FRAMEWIDTH, FRAMEHEIGHT);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        getContentPane().add(splitPane);
        
        JScrollPane scrollPane = new JScrollPane(new BoardPanel());
        
        /*JScrollPane scrollPane = new JScrollPane();
        JPanel tmp = new JPanel();
        tmp.setLayout(new GridBagLayout());
        tmp.add(new BoardPanel());
        scrollPane.add(tmp);*/
        
        splitPane.setLeftComponent(scrollPane);
        
        JPanel optionsPanel = new JPanel();
        splitPane.setRightComponent(optionsPanel);
        
        setVisible( true );
        validate();

        server = s;
    }
}

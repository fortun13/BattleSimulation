package main.java.gui;

import main.java.adapters.Controller;
import main.java.agents.ServerAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class MainFrame extends JFrame {

    private final int FRAMEHEIGHT = 700;

	private final int FRAMEWIDTH = 800;

	private BoardPanel boardPanel;
	private OptionsPanel optionsPanel;

    public ServerAgent server;
    
    private Controller controller;

    public MainFrame(ServerAgent s) {

        setTitle("Battle!");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        setSize(FRAMEWIDTH, FRAMEHEIGHT);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        getContentPane().add(splitPane);
        
        boardPanel = new BoardPanel();
        JScrollPane scrollPane = new JScrollPane(boardPanel);
        
        /*JScrollPane scrollPane = new JScrollPane();
        JPanel tmp = new JPanel();
        tmp.setLayout(new GridBagLayout());
        tmp.add(new BoardPanel());
        scrollPane.add(tmp);*/
        
        splitPane.setLeftComponent(scrollPane);
        
        optionsPanel = new OptionsPanel();
        splitPane.setRightComponent(optionsPanel);
        
        setVisible( true );
        validate();

        server = s;

        controller = new Controller(this, boardPanel, optionsPanel);
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                server.doDelete();
//            }
//        });
    }
}

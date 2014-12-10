package main.java.gui;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import main.java.adapters.Controller;
import main.java.agents.ServerAgent;
import main.java.utils.AgentInTree;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Created by Jakub Fortunka on 08.11.14.
 *
 */
public class MainFrame extends JFrame {

    private final int FRAMEHEIGHT = 700;

	private final int FRAMEWIDTH = 900;

    private BoardPanel boardPanel;
	private OptionsPanel optionsPanel;
    
    private Controller controller;

    public ServerAgent server;

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

        splitPane.setLeftComponent(scrollPane);
        
        optionsPanel = new OptionsPanel();
        splitPane.setRightComponent(optionsPanel);
        
        setVisible( true );
        validate();

        server = s;

        controller = new Controller(this,server);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.doDelete();
            }

        });


    }

    public void redrawBoard(KDTree<AgentInTree> agents) {
        double[] testKey = {0,0};
        double[] upperKey = {1000,1000};
        List<AgentInTree> lst = null;
        try {
            lst = agents.range(testKey,upperKey);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
        boardPanel.drawAgents(lst);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public OptionsPanel getOptionsPanel() {
        return optionsPanel;
    }
    
}

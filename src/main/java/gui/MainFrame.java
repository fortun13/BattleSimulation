package main.java.gui;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import main.java.adapters.Controller;
import main.java.agents.ServerAgent;
import main.java.utils.AgentInTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
    private final JButton btnStartSimulation;

    private BoardPanel boardPanel;
	private OptionsPanel optionsPanel;
    
    private Controller controller;

    public ServerAgent server;

    public MainFrame(ServerAgent s) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle(Messages.getString("MainFrame.this.title")); //$NON-NLS-1$

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        setSize(FRAMEWIDTH, FRAMEHEIGHT);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JTabbedPane tabs = new JTabbedPane();

        //JSplitPane splitPane = new JSplitPane();
        //splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //getContentPane().add(splitPane);
        
        boardPanel = new BoardPanel();
        JScrollPane scrollPane = new JScrollPane(boardPanel);

        JPanel boardTabPanel = new JPanel();

        boardTabPanel.setLayout(new BorderLayout());

        boardTabPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel oneButtonPanel = new JPanel();

        btnStartSimulation = new JButton(Messages.getString("MainFrame.btnStartSimulation.text")); //$NON-NLS-1$
        oneButtonPanel.add(btnStartSimulation);

        boardTabPanel.add(oneButtonPanel,BorderLayout.SOUTH);

        tabs.addTab(Messages.getString("MainFrame.tab1.tabName"),null, boardTabPanel,Messages.getString("MainFrame.tab1.tabTooltip"));


        //splitPane.setLeftComponent(scrollPane);
        
        optionsPanel = new OptionsPanel();
        //splitPane.setRightComponent(optionsPanel);

        tabs.addTab(Messages.getString("MainFrame.tab2.tabName"),null,optionsPanel,Messages.getString("MainFrame.tab2.tabTooltip"));

        getContentPane().add(tabs);
        
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

    public void startSimulationButtonAddActionListener(ActionListener listener) {
        btnStartSimulation.addActionListener(listener);
    }
}

package main.java.gui;

import main.java.adapters.Controller;
import main.java.agents.ServerAgent;
import main.java.utils.AgentInTree;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

    private final JButton btnStartSimulation;
    private final JButton spawnAgents;
    private final JLabel lblConditionState;
    private final JLabel lblMorale;
    private final int FRAME_WIDTH = 1200;
    private final int FRAME_HEIGHT = 700;

    private BoardPanel.MyAgent clickedAgent;

    private BoardPanel boardPanel;
	private OptionsPanel optionsPanel;
    
    private Controller controller;

    public ServerAgent server;

    public MainFrame(ServerAgent s) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        setTitle(Messages.getString("MainFrame.this.title")); //$NON-NLS-1$

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JTabbedPane tabs = new JTabbedPane();

        boardPanel = new BoardPanel();
        JScrollPane scrollPane = new JScrollPane(boardPanel);

        JPanel boardTabPanel = new JPanel();

        boardTabPanel.setLayout(new BorderLayout());

        boardTabPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        
        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setBorder(new TitledBorder(null, Messages.getString("MainFrame.statisticsPanel.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
        lowerPanel.add(statisticsPanel);
        statisticsPanel.setLayout(new GridLayout(0, 2, 0, 0));
        
        JLabel lblCondition = new JLabel(Messages.getString("MainFrame.lblCondition.text")); //$NON-NLS-1$
        statisticsPanel.add(lblCondition);
        
        lblConditionState = new JLabel("");
        statisticsPanel.add(lblConditionState);

        JLabel lblMor = new JLabel(Messages.getString("MainFrame.lblMorale.text"));
        statisticsPanel.add(lblMor);

        lblMorale = new JLabel("");
        statisticsPanel.add(lblMorale);

        boardTabPanel.add(lowerPanel,BorderLayout.SOUTH);
        
        JPanel buttonsPanel = new JPanel();
        lowerPanel.add(buttonsPanel);
                        
        spawnAgents = new JButton(Messages.getString("MainFrame.spawnAgents.text")); //$NON-NLS-1$
        buttonsPanel.add(spawnAgents);
        spawnAgents.setAlignmentX(Component.CENTER_ALIGNMENT);
                
        btnStartSimulation = new JButton(Messages.getString("MainFrame.btnStartSimulation.text"));
        buttonsPanel.add(btnStartSimulation);

        tabs.addTab(Messages.getString("MainFrame.tab1.tabName"),null, boardTabPanel,Messages.getString("MainFrame.tab1.tabTooltip"));

        optionsPanel = new OptionsPanel();

        tabs.addTab(Messages.getString("MainFrame.tab2.tabName"),null,optionsPanel,Messages.getString("MainFrame.tab2.tabTooltip"));

        getContentPane().add(tabs);
        
        setVisible( true );
        validate();

        server = s;

        controller = new Controller(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.doDelete();
            }

        });

    }

    public void redrawBoard(List<AgentInTree> agents) {
        boardPanel.drawAgents(agents);
    }

    public void spawnAgentsAddActionListener(ActionListener listener) {
        spawnAgents.addActionListener(listener);
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

    /**
     * Method updates statistics of clicked agent to the panel below board
     *
     * @param agent clicked agent
     */
    public void updateStatistics(BoardPanel.MyAgent agent) {
        clickedAgent = agent;
        updateStatistics();
    }

    /**
     * Updates showed statistics of clicked agent
     */
    public void updateStatistics() {
        if (clickedAgent == null)
            return;
        lblConditionState.setText(String.valueOf(clickedAgent.getAgent().condition));
        lblConditionState.setForeground(Color.RED);
        lblMorale.setText(String.valueOf(clickedAgent.getAgent().morale));
        lblMorale.setForeground(Color.RED);
    }

    public void cleanStatistics() {
        clickedAgent = null;
        lblConditionState.setText("");
        lblMorale.setText("");
    }

    public double getFRAME_WIDTH() {
        return FRAME_WIDTH;
    }

    public double getFRAME_HEIGHT() {
        return FRAME_HEIGHT;
    }
}

package main.java.gui;

import edu.wlu.cs.levy.CG.KeySizeException;
import javafx.util.Pair;
import main.java.adapters.BoardMouseListener;
import main.java.adapters.BoardMouseMotionListener;
import main.java.adapters.BoardMouseWheelListener;
import main.java.agents.ServerAgent;
import main.java.utils.AgentInTree;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Jakub Fortunka on 08.11.14.
 *
 */
public class MainFrame extends JFrame {
    public ServerAgent server;
    private BoardPanel.MyAgent clickedAgent;

    private BoardMouseMotionListener motionListener = null;
    private BoardMouseListener mouseListener = null;
    private BoardMouseWheelListener mouseWheelListener = null;

    private BoardPanel boardPanel;
    private OptionsPanel optionsPanel;
    private final JButton btnStartSimulation;
    private final JButton spawnAgents;
    private final JLabel lblConditionState;
    private final JLabel lblMorale;
    private final int FRAME_WIDTH = 1200;
    private final int FRAME_HEIGHT = 700;

    public MainFrame(ServerAgent s) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        server = s;

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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.doDelete();
            }

        });
        PrepareListeners prepare = new PrepareListeners(this);
        prepare.PrepareToListen();
    }

    public void redrawBoard(List<AgentInTree> agents) {
        boardPanel.drawAgents(agents);
    }

    public void spawnAgentsAddActionListener(ActionListener listener) {
        spawnAgents.addActionListener(listener);
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

    public static <T extends Container> T findParent(Component comp, Class<T> clazz)  {
        if (comp == null)
            return null;
        if (clazz.isInstance(comp))
            return (clazz.cast(comp));
        else
            return findParent(comp.getParent(), clazz);
    }

    /* Getery i setery */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
    public OptionsPanel getOptionsPanel() {
        return optionsPanel;
    }

    public double getFRAME_WIDTH() {
        return FRAME_WIDTH;
    }
    public double getFRAME_HEIGHT() {
        return FRAME_HEIGHT;
    }

    /* Prywatna klasa do przygotowania listenerów */
    private class PrepareListeners {
        private MainFrame frame;
        public PrepareListeners(MainFrame frame){
            this.frame = frame;
        }

        public void PrepareToListen() {
            createOpenFileActionListener();
            createSaveToFileActionListener();

            this.startSimulationButtonAddActionListener(e -> {
                if (frame.mouseWheelListener == null) {
                    mouseWheelListener = new BoardMouseWheelListener(frame.getBoardPanel());
                    frame.getBoardPanel().addMouseWheelListener(mouseWheelListener);
                } else {
                    mouseWheelListener.simulationStarted = true;
                }

                motionListener.simulationStarted = true;
                mouseListener.simulationStarted = true;
                frame.getBoardPanel().simulationStarted = true;
                frame.server.startSimulation();
            });

            frame.getOptionsPanel().setSidePanelsSliderListener(e -> {
                JSlider source = (JSlider) e.getSource();
                SideOptionPanel p = findParent(source, SideOptionPanel.class);
                p.sliderMoved();
            });

            frame.spawnAgentsAddActionListener((e) -> {
                Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
                prepareMouseListenersAndBoard(size.getValue(), size.getKey());
                frame.server.prepareSimulation(frame.getOptionsPanel().getBluesAgents(), frame.getOptionsPanel().getRedsAgents(), null, frame.getOptionsPanel().getTimeStep());
            });
        }

        public void spawnAgentsAddActionListener(ActionListener listener) {
            spawnAgents.addActionListener(listener);
        }

        public void startSimulationButtonAddActionListener(ActionListener listener) {
            btnStartSimulation.addActionListener(listener);
        }

        private void prepareMouseListenersAndBoard(int boardWidth, int boardHeight) {
            if (mouseWheelListener != null) {
                mouseWheelListener.simulationStarted = false;
            }
            if (motionListener == null) {
                motionListener = new BoardMouseMotionListener(frame.getBoardPanel());
                mouseListener = new BoardMouseListener(frame.getBoardPanel());
                frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
                frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
            } else {
                motionListener.simulationStarted = false;
                mouseListener.simulationStarted = false;
            }
            frame.getBoardPanel().simulationStarted = false;
            frame.getBoardPanel().generateBoard(boardHeight, boardWidth);
            frame.getBoardPanel().resetScale();
        }

        private void createOpenFileActionListener() {
            frame.getOptionsPanel().openFileAddActionListener(e -> {
                int returnVal = frame.getOptionsPanel().getFileChooser().showOpenDialog(frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = frame.getOptionsPanel().getFileChooser().getSelectedFile();
                    //This is where a real application would open the file.
                    String content = "";
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(new FileInputStream(file));
                        while (scanner.hasNext())
                            content += scanner.nextLine();

                        scanner.close();
                        JSONObject obj = new JSONObject(content);
                        JSONArray agents = obj.getJSONArray("agents");

                        HashMap<String, ArrayList<JSONObject>> map = new HashMap<>();

                        for (int i = 0; i < agents.length(); i++) {
                            JSONObject agent = agents.getJSONObject(i);

                            if (map.containsKey(agent.get("type").toString()))
                                map.get(agent.get("type").toString()).add(agent);
                            else {
                                ArrayList<JSONObject> lst = new ArrayList<>();
                                lst.add(agent);
                                map.put(agent.get("type").toString(), lst);
                            }
                        }
                        prepareMouseListenersAndBoard(obj.getInt("boardWidth"),obj.getInt("boardHeight"));
                        //frame.server.prepareSimulation(null,null,map, obj.getInt("boardWidth"),frame.getOptionsPanel().getTimeStep());
                        frame.server.prepareSimulation(null,null,map,frame.getOptionsPanel().getTimeStep());
                        frame.getOptionsPanel().setBoardSize(obj.getInt("boardWidth"),obj.getInt("boardHeight"));
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        private void createSaveToFileActionListener() {
            frame.getOptionsPanel().saveToFileAddActionListener(e -> {
                int returnVal = frame.getOptionsPanel().getFileChooser().showSaveDialog(frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = frame.getOptionsPanel().getFileChooser().getSelectedFile();
                    try {
                        FileWriter fw = new FileWriter(f);
                        Pair<Integer,Integer> size = frame.getOptionsPanel().getBoardSize();
                        JSONWriter w = new JSONWriter(fw);
                        w.object();
                        w.key("boardWidth").value(size.getValue());
                        w.key("boardHeight").value(size.getKey());

                        frame.server.updateTree();

                        double[] testKey = {0,0};
                        double[] upperKey = {size.getValue()*frame.getBoardPanel().SQUARESIZE,size.getKey()*frame.getBoardPanel().SQUARESIZE};
                        try {
                            java.util.List<AgentInTree> lst = frame.server.getWorld().getAgentsTree().range(testKey, upperKey);
                            w.key("agents");
                            w.array();
                            for (AgentInTree a : lst) {
                                w.object();
                                w.key("x").value(a.p.getX());
                                w.key("y").value(a.p.getY());
                                w.key("type").value(a.type);
                                w.key("side").value(a.side);
                                if (a.behaviourClass != null) {
                                    String b = a.behaviourClass.getName();
                                    w.key("behaviour").value(b.substring(b.lastIndexOf('.')+1));
                                }
                                else
                                    w.key("behaviour").value("");
                                w.endObject();
                            }
                            w.endArray();
                            //w.endObject();
                        } catch (KeySizeException e1) {
                            e1.printStackTrace();
                        }
                        w.endObject();
                        fw.flush();
                        fw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });
        }
    }

}


package gui;

import agents.AgentType;
import edu.wlu.cs.levy.CG.KeySizeException;
import javafx.util.Pair;
import adapters.BoardMouseListener;
import adapters.BoardMouseMotionListener;
import adapters.BoardMouseWheelListener;
import agents.ServerAgent;
import agents.World;
import utils.AgentInTree;
import utils.SquareSize;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

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
    private ServerAgent server;
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

    public ServerAgent getServer() {
        return server;
    }

    /* Prywatna klasa do przygotowania listenerów */
    private class PrepareListeners {
        private MainFrame frame;
        public PrepareListeners(MainFrame frame){
            this.frame = frame;
        }

        private static final int BoardWidth = 0, BoardHeight = 1, StepSize = 2, RoundsNumber = 3, RoundsLimit = 4;

        private String[] basicParameters = {"boardWidth","boardHeight","step-size","rounds-number","rounds-limit"};

        private static final int AgentSize = 0,AngleOfView = 1, RangeOfView = 2, Temporize = 3, FollowingWeight = 4, SeekCenterWeight = 5,
                AvoidingWeight = 6, MinimalDistance = 7;

        private String[] boidsParametersNames = {"agent-size","angle-of-view","range-of-view","temporize","following-weight",
                "seek-center-weight", "avoiding-weight","minimal-distance"};

        private static final int Condition = 0,Strength = 1,Speed = 2, Accuracy = 3,Range = 4,AttractionForce=5;

        private String[] parametersNames = {"condition","strength","speed","accuracy","range","attraction-force"};

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
                SideOptionsPanel p = findParent(source, SideOptionsPanel.class);
                p.sliderMoved();
            });

            frame.spawnAgentsAddActionListener((e) -> {
                Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
                prepareMouseListenersAndBoard(size.getValue(), size.getKey());
                frame.server.prepareSimulation(frame.getOptionsPanel().getBluesAgents(), frame.getOptionsPanel().getRedsAgents(), frame.getOptionsPanel().getTimeStep());
            });
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

                        setBasicParametersFromJson(obj);
                        setBoidsParametersFromJson(obj);
                        setAgentParametersFromJson(obj);

                        JSONArray agents = obj.getJSONArray("agents");

                        HashMap<AgentType, ArrayList<JSONObject>> blues = new HashMap<>();

                        HashMap<AgentType, ArrayList<JSONObject>> reds = new HashMap<>();
                        ArrayList<JSONObject> obstacles = new ArrayList<>();

                        for (int i = 0; i < agents.length(); i++) {
                            JSONObject agent = agents.getJSONObject(i);
                            World.AgentsSides as = World.AgentsSides.valueOf(agent.getString("side"));
                            switch (as) {
                                case Blues:
                                    addAgentToProperList(blues, agent);
                                    break;
                                case Reds:
                                    addAgentToProperList(reds, agent);
                                    break;
                                case Obstacle:
                                    obstacles.add(agent);
                                    break;
                            }
                        }
                        frame.server.prepareSimulation(blues, reds, obstacles, optionsPanel.getTimeStep());

                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        private void addAgentToProperList(HashMap<AgentType,ArrayList<JSONObject>> side, JSONObject agent) {
            AgentType key = AgentType.valueOf(agent.getString("type").toUpperCase());
            ArrayList<JSONObject> l = side.get(key);
            if (l==null)
                l = new ArrayList<>();
            l.add(agent);
            side.put(key, l);
        }

        private void setBasicParametersFromJson(JSONObject o) {
            int bw = o.getInt(basicParameters[BoardWidth]);
            int bh = o.getInt(basicParameters[BoardHeight]);
            optionsPanel.setBoardSize(bw, bh);
            optionsPanel.setTimestep(o.getInt(basicParameters[StepSize]));
            optionsPanel.setTurnNumber(o.getInt(basicParameters[RoundsNumber]));
            optionsPanel.setLimitCheckbox(o.getBoolean(basicParameters[RoundsLimit]));
            prepareMouseListenersAndBoard(bw, bh);
        }

        private void setBoidsParametersFromJson(JSONObject o) {
            JSONObject parameters = o.getJSONObject("boids");
            BoidOptions bo = optionsPanel.boidOptions;
            bo.setAgentSize(parameters.getInt(boidsParametersNames[AgentSize]));
            bo.setAngleOfView(parameters.getInt(boidsParametersNames[AngleOfView]));
            bo.setAvoidingWeight(parameters.getInt(boidsParametersNames[AvoidingWeight]));
            bo.setFollowingWeight(parameters.getInt(boidsParametersNames[FollowingWeight]));
            bo.setMinimalDistance(parameters.getInt(boidsParametersNames[MinimalDistance]));
            bo.setSeekCenterWeight(parameters.getInt(boidsParametersNames[SeekCenterWeight]));
            bo.setRangeOfView(parameters.getInt(boidsParametersNames[RangeOfView]));
            bo.setTemporize(parameters.getInt(boidsParametersNames[Temporize]));
        }

        public void setAgentParametersFromJson(JSONObject o) {
            JSONArray a = o.getJSONArray("parameters");
            for (int i=0;i<a.length();i++) {
                JSONObject side = a.getJSONObject(i);
                SideOptionsPanel op = (side.getString("side").equals("Blues")) ? optionsPanel.bluePanel : optionsPanel.redPanel;
                JSONArray state = side.getJSONArray("state");
                for (int j=0;j<state.length();j++) {
                    JSONObject type = state.getJSONObject(j);
                    AgentType at = AgentType.valueOf(type.getString("type").toUpperCase());
                    op.setCondition(at,type.getInt(parametersNames[Condition]));
                    op.setStrength(at,type.getInt(parametersNames[Strength]));
                    op.setSpeed(at,type.getInt(parametersNames[Speed]));
                    op.setAccuracy(at,type.getInt(parametersNames[Accuracy]));
                    op.setRange(at,type.getInt(parametersNames[Range]));
                    if (at == AgentType.COMMANDER)
                        op.setAttractionForce(type.getInt(parametersNames[AttractionForce]));
                }
            }
        }

        private void createSaveToFileActionListener() {
            frame.getOptionsPanel().saveToFileAddActionListener(e -> {
                int returnVal = optionsPanel.getFileChooser().showSaveDialog(frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = frame.getOptionsPanel().getFileChooser().getSelectedFile();
                    try {
                        FileWriter fw = new FileWriter(f);
                        Pair<Integer,Integer> size = optionsPanel.getBoardSize();
                        JSONWriter w = new JSONWriter(fw);

                        // Basic Parameters
                        saveBasicParametersToJson(w,size);
                        // Boids parameters
                        saveBoidsParametersToJson(w);
                        // Agents parameters
                        saveAgentsParametersToJson(w);

                        // in case user added some obstacles
                        server.updateTree();

                        // Agents
                        saveAgentsToJson(w, size);

                        w.endObject();
                        fw.flush();
                        fw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });
        }

        private void saveBasicParametersToJson(JSONWriter w, Pair<Integer,Integer> size) {
            int[] parameters = new int[basicParameters.length-1];
            parameters[BoardHeight] = size.getKey();
            parameters[BoardWidth] = size.getValue();
            parameters[StepSize] = optionsPanel.getTimeStep();
            parameters[RoundsNumber] = optionsPanel.getTurnsLimit();
            w.object();
            for (int i=0;i<parameters.length;i++)
                w.key(basicParameters[i]).value(parameters[i]);
            w.key(basicParameters[RoundsLimit]).value(optionsPanel.limitIsActive());
        }

        private void saveBoidsParametersToJson(JSONWriter w) {
            // Boids
            BoidOptions bo = optionsPanel.boidOptions;
            double[] boidsValues = new double[boidsParametersNames.length];
            boidsValues[AgentSize] = bo.getAgentSize();
            boidsValues[AngleOfView] = bo.getAngleOfView();
            boidsValues[RangeOfView] = bo.getRangeOfView();
            boidsValues[Temporize] = bo.getTemporize();
            boidsValues[FollowingWeight] = bo.getFollowingWeight()*100;
            boidsValues[SeekCenterWeight] = bo.getSeekCenterWeight()*100;
            boidsValues[AvoidingWeight] = bo.getAvoidingWeight()*100;
            boidsValues[MinimalDistance] = bo.getMinimalDistance();
            w.key("boids");
            w.object();
            for (int i=0;i<boidsParametersNames.length;i++)
                w.key(boidsParametersNames[i]).value(boidsValues[i]);
            w.endObject();
        }

        private void saveAgentsParametersToJson(JSONWriter w) {
            // Parameters
            w.key("parameters");
            w.array();

            generateParametersForSide(World.AgentsSides.Blues,w);
            generateParametersForSide(World.AgentsSides.Reds,w);

            w.endArray();
        }

        private void saveAgentsToJson(JSONWriter w, Pair<Integer, Integer> size) {
            // Agents
            double[] testKey = {0,0};
            double[] upperKey = {size.getValue()*SquareSize.getInstance().getValue(),size.getKey()*SquareSize.getInstance().getValue()};
            try {
                java.util.List<AgentInTree> lst = server.getWorld().getAgentsTree().range(testKey, upperKey);
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
        }

        private void generateParametersForSide(World.AgentsSides side, JSONWriter w) {
            w.object();
            w.key("side").value(side);
            w.key("state");
            w.array();
            for (Pair<String, ArrayList<Pair<String,Integer>>> types : getParametersFromSidePanel(side)) {
                w.object();
                w.key("type").value(types.getKey());
                for (Pair<String,Integer> p : types.getValue())
                    w.key(p.getKey()).value(p.getValue());
                w.endObject();
            }
            w.endArray();
            w.endObject();
        }

        /**
         * @param side side of which parameters we want to have
         * @return List of Pairs of String(name of type (ex. Warrior)) and list of pairs of String(ParameterName) and Integer(ParameterValue)
         * ArrayList<Pair<NameOfType,ArrayList<Pair<ParameterName,ParameterValue>>>>
         *
         */
        private ArrayList<Pair<String,ArrayList<Pair<String,Integer>>>> getParametersFromSidePanel(World.AgentsSides side) {
            SideOptionsPanel p = (side == World.AgentsSides.Blues) ? optionsPanel.bluePanel : optionsPanel.redPanel;
            ArrayList<Pair<String,ArrayList<Pair<String,Integer>>>> lst = new ArrayList<>();
            for (AgentType t : AgentType.values()) {
                if (t != AgentType.OBSTACLE) {
                    ArrayList<Pair<String,Integer>> l = new ArrayList<>();
                    l.add(new Pair<>(parametersNames[Condition],p.getCondition(t)));
                    l.add(new Pair<>(parametersNames[Strength],p.getStrength(t)));
                    l.add(new Pair<>(parametersNames[Speed],p.getSpeed(t)));
                    l.add(new Pair<>(parametersNames[Accuracy],p.getAccuracy(t)));
                    l.add(new Pair<>(parametersNames[Range],p.getRange(t)));
                    if (t == AgentType.COMMANDER)
                        l.add(new Pair<>(parametersNames[AttractionForce],p.getAttractionForce()));
                    lst.add(new Pair<>(t.toString(),l));
                }
            }
            return lst;
        }

    }

}


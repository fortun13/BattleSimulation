package main.java.gui;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import javafx.scene.shape.Circle;
import main.java.adapters.Controller;
import main.java.agents.ServerAgent;
import main.java.agents.World;
import main.java.utils.KdTree;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class MainFrame extends JFrame {

    private final int FRAMEHEIGHT = 700;

	private final int FRAMEWIDTH = 800;

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

        controller = new Controller(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.doDelete();
            }

        });

        server = s;
    }

    public void redrawBoard(KdTree.StdKd<World.AgentComparator.AgentSpace> agents) {
        HashSet<World.AgentsSides> sides = new HashSet<World.AgentsSides>();
        sides.add(World.AgentsSides.Blues);
        sides.add(World.AgentsSides.Reds);
        List<? extends KdTree.Placed> ag = agents.fetchElements(new World.AgentComparator.AgentSpace(sides, new Circle(0, 0, 1000)));
        //((World.AgentInTree)ag.get(0)).isDead = false;
        boardPanel.drawAgents(ag);
    }

    public void redrawBoard2(KDTree<World.AgentInTree> agents) {
        double[] testKey = {0,0};
        double[] upperKey = {1000,1000};
        List<World.AgentInTree> lst = null;
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

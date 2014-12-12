package main.java.agents;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeyMissingException;
import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import main.java.gui.BoardPanel;
import main.java.gui.MainFrame;
import main.java.utils.AgentInTree;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jakub Fortunka on 08.11.14.
 *
 */
public class ServerAgent extends Agent {

    // Instance variables
    //////////////////////////////////
    protected MainFrame m_frame = null;
    private World world = null;
    private int agentsNumber;

    private long timestep=0;

    Behaviour serverBehaviour = new Behaviour() {

        int state = 0;
        int agentsCounter = 0;
        int stepsCounter = 0;

        long time;

        long interval = 0;
        private ACLMessage newTurn;

        @Override
        public void action() {

            if (newTurn == null)
                initMessages();

            switch (state) {
                case 0:
                    if (world.redsAgents.size() == 0 || world.bluesAgents.size() == 0) {
                        state = 2;
                        System.out.println("Turn: " + stepsCounter);
                        ACLMessage endBattle = new ACLMessage(ACLMessage.INFORM);
                        world.redsAgents.forEach(endBattle::addReceiver);
                        world.bluesAgents.forEach(endBattle::addReceiver);
                        endBattle.setConversationId("battle-ended");
                        send(endBattle);
                        m_frame.redrawBoard(world.getAgentsTree());
                        break;
                    }
                    send(newTurn);
                    time = System.currentTimeMillis();
                    state = 1;
                    System.out.println("Turn: " + stepsCounter);
                    break;
                case 1:
                    ACLMessage msg = receive();
                    if (msg != null) {
                        if (msg.getConversationId().equals("ended-computation")) {
                            agentsCounter++;
                            if (agentsCounter == agentsNumber) {
                                agentsCounter = 0;
                                stepsCounter++;

                                m_frame.redrawBoard(world.getAgentsTree());
                                m_frame.updateStatistics();
                                while (System.currentTimeMillis() - time < interval)
                                    block(interval - (System.currentTimeMillis() - time));

                                state = 0;
                                break;
                            }
                        } else if (msg.getConversationId().equals("agent-dead")) {
                            while (System.currentTimeMillis() - time < interval)
                                block(interval - (System.currentTimeMillis() - time));
                        }
                    } else {
                        block();
                    }
            }
        }

        @Override
        public boolean done() {
                return state == 2;
        }

        void initMessages() {
            newTurn = new ACLMessage(ACLMessage.INFORM);
            world.bluesAgents.forEach(newTurn::addReceiver);
            world.redsAgents.forEach(newTurn::addReceiver);
            newTurn.setConversationId("new-turn");
            interval = getTimestep();
            System.out.println("Interval: " + interval);
        }

        public void reset() {
            removeBehaviour(this);
            state = 0;
            agentsCounter = 0;
            stepsCounter = 0;
            initMessages();
        }

    };

    public ServerAgent() {
        super();
    }

    /**
     * Setup the agent.  Registers with the DF, and adds a behaviour to
     * process incoming messages.
     */
    protected void setup() {
        System.out.println( getLocalName() + " setting up");
        setupUI();
    }

    /**
     * Setup the UI, which means creating and showing the main frame.
     */
    private void setupUI() {
        m_frame = new MainFrame(this);
    }

    public void prepareSimulation(ArrayList<Pair<World.AgentType,Integer>> blues,
                                  ArrayList<Pair<World.AgentType,Integer>> reds,
                                  HashMap<String,ArrayList<JSONObject>> map,
                                  int boardWidth,
                                  long timestep) {
        this.timestep = timestep;
        if (world != null) {
            world.clean();
            doWait(100);
        }
        if (map == null)
            world = new World(this,blues,reds);
        else
            world = new World(this,map, boardWidth);

        serverBehaviour.reset();
        m_frame.redrawBoard(world.getAgentsTree());
    }

    public void startSimulation() {
        updateTree();
        System.out.println("Simulation started");
        updateState();
        addBehaviour(serverBehaviour);
    }

    public long getTimestep() {
        return timestep;
    }

    private void updateTree() {
        List<BoardPanel.MyAgent> changed = m_frame
                .getBoardPanel()
                .getMyAgents()
                .parallelStream()
                .filter(e -> !e.getAgent().p.equals(e.getPoint()))
                .collect(Collectors.toList());

        for (BoardPanel.MyAgent a : changed) {
            AgentInTree position = a.getAgent();
            double[] oldPos = {position.p.getX(), position.p.getY()};
            double[] newPos = {a.getPoint().getX(), a.getPoint().getY()};

            position.setPosition(a.getPoint());
            try {
                world.getAgentsTree().delete(oldPos);
                world.getAgentsTree().insert(newPos, position);
            } catch (KeySizeException | KeyDuplicateException | KeyMissingException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateState() {
        agentsNumber = world.bluesAgents.size() + world.redsAgents.size();
    }

    public MainFrame getFrame() {
        return m_frame;
    }
}

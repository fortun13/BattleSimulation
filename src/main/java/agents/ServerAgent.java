package agents;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import gui.BoardPanel;
import gui.MainFrame;
import utils.AgentInTree;
import utils.SquareSize;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        World.AgentsSides[] sides = {World.AgentsSides.Blues, World.AgentsSides.Reds};

        BufferedWriter stats;

        long interval = 300;
        private ACLMessage newTurn;

        @Override
        public void action() {

            if (newTurn == null)
                initMessages();

            switch (state) {
                case 0:
                    if ((world.redsAgents.size() == 0 || world.bluesAgents.size() == 0)
                            || (m_frame.getOptionsPanel().limitIsActive()
                            && m_frame.getOptionsPanel().getTurnsLimit() <= stepsCounter)) {
                        state = 2;
                        System.out.println("Turn: " + stepsCounter);
                        //obliczanie stosunku strat do l. początkowej
                        double victoryCon = computeVictoryCondition();
                        if(victoryCon > 0) {
                            ACLMessage endBattleVictory = new ACLMessage(ACLMessage.INFORM);
                            world.bluesAgents.forEach(endBattleVictory::addReceiver);
                            endBattleVictory.setConversationId("battle-ended-victory");
                            send(endBattleVictory);
                            ACLMessage endBattleLoss = new ACLMessage(ACLMessage.INFORM);
                            world.redsAgents.forEach(endBattleLoss::addReceiver);
                            endBattleLoss.setConversationId("battle-ended-loss");
                            send(endBattleLoss);
                        } else if(victoryCon < 0) {
                            ACLMessage endBattleVictory = new ACLMessage(ACLMessage.INFORM);
                            world.redsAgents.forEach(endBattleVictory::addReceiver);
                            endBattleVictory.setConversationId("battle-ended-victory");
                            send(endBattleVictory);
                            ACLMessage endBattleLoss = new ACLMessage(ACLMessage.INFORM);
                            world.bluesAgents.forEach(endBattleLoss::addReceiver);
                            endBattleLoss.setConversationId("battle-ended-loss");
                            send(endBattleLoss);
                            /*wiadomość trzeba by też posłać trupom - jak padną wszyscy to nikt jej nie odbierze...
                            * Zresztą - czy wiadomośc o wyniku ma jakieś znaczenie poza upiększająco-wizualnym?*/
                        } else {
                            ACLMessage endBattle = new ACLMessage(ACLMessage.INFORM);
                            world.redsAgents.forEach(endBattle::addReceiver);
                            world.bluesAgents.forEach(endBattle::addReceiver);
                            endBattle.setConversationId("battle-ended-draw");
                            send(endBattle);
                        }

                        try {
                            m_frame.redrawBoard(getAllAgents());
                        } catch (KeySizeException e) {
                            e.printStackTrace();
                        }
                        try {
                            generateStatistics();
                            stats.flush();
                            stats.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    send(newTurn);
                    generateStatistics();
                    time = System.currentTimeMillis();
                    state = 1;
                    System.out.println("Turn: " + stepsCounter);
                    break;
                case 1:
                    ACLMessage msg = receive();
                    if (msg != null) {
                        if (msg.getConversationId().equals("ended-computation")) {
                            agentsCounter++;
                            //System.out.println("Agents number: " + agentsCounter);
                            if (agentsCounter == agentsNumber) {
                                agentsCounter = 0;
                                stepsCounter++;
                                //System.out.println("I'm in if!!");

                                try {
                                    m_frame.redrawBoard(getAllAgents());
                                } catch (KeySizeException e) {
                                    e.printStackTrace();
                                }
                                //System.out.println("I've redraw board!!");
                                m_frame.updateStatistics();
                                long tmp = System.currentTimeMillis() - time;
                                //System.out.println("before waiting");
                                if (tmp < interval)
                                    block(interval - tmp);
                                //System.out.println("after waiting");
                                state = 0;
                                break;
                            }
                        } else if (msg.getConversationId().equals("agent-dead")) {
                            long tmp = System.currentTimeMillis() - time;
                            if (tmp < interval)
                                block(interval - tmp);
                        }
                    } else {
                        block();
                    }
            }
        }

        private void generateStatistics() {
            String line = "";
            for (World.AgentsSides s : sides) {
                line += s.toString() + ": ";
                for (AgentType t : AgentType.values()) {
                    try {
                        line += t.toString() + ": " + getAllAgents()
                                .parallelStream()
                                .filter(a -> a.side == s && a.type == t)
                                .count() + " ";
                    } catch (KeySizeException e) {
                        e.printStackTrace();
                    }
                }
            }
            line += ";";
            saveStatsToFile(line);
        }

        private void saveStatsToFile(String line) {
            try {
                stats.write(line);
                stats.newLine();
            } catch (IOException e) {
                e.printStackTrace();
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
            initStatisticsFile();
            //System.out.println("Interval: " + interval);
        }

        private void initStatisticsFile() {
            try {
                stats = new BufferedWriter(new FileWriter(new File(String.valueOf(System.currentTimeMillis()) + ".dat")));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    /**
     * method resposible for preparing simulation (populates the world etc.)
     * @param blues list of agents of blue side, which we want in the world
     * @param reds list of agents of red side which we want in the world
     * @param timestep time of every turn (in milliseconds)
     */
    public void prepareSimulation(ArrayList<Pair<AgentType,Integer>> blues,
                                  ArrayList<Pair<AgentType,Integer>> reds,
                                  long timestep) {

        prepare(timestep);
        world = new World(this,blues,reds);
        start();
    }

    public void prepareSimulation(HashMap<AgentType,ArrayList<JSONObject>> blues,
                                  HashMap<AgentType,ArrayList<JSONObject>> reds,
                                  ArrayList<JSONObject> obstacles,
                                  long timestep) {
        prepare(timestep);
        world = new World(this,blues,reds,obstacles);
        start();
    }

    private void prepare(long timestep) {
        this.timestep = timestep;
        if (world != null) {
            world.clean();
            doWait(100);
        }
    }

    private void start() {
        serverBehaviour.reset();
        try {
            m_frame.redrawBoard(getAllAgents());
        } catch (KeySizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * method which starts the simulation
     */
    public void startSimulation() {
        updateTree();
        System.out.println("Simulation started");
        updateState();
        addBehaviour(serverBehaviour);
    }

    public long getTimestep() {
        return timestep;
    }

    /**
     * method which updates tree in world
     */
    public void updateTree() {
        List<BoardPanel.MyAgent> changed = m_frame
                .getBoardPanel()
                .getMyAgents()
                .parallelStream()
                .filter(e -> !e.getAgent().p.equals(e.getPoint()))
                .collect(Collectors.toList());

        world.updateTree(changed);
    }

    protected double computeVictoryCondition() {
        double deathReds = world.redsCorpses.size();
        double allReds = world.redsAgents.size();
        double deathBlues = world.bluesCorpses.size();
        double allBlues = world.bluesAgents.size();
        return (deathReds/(allReds + deathReds)) - (deathBlues/(allBlues + deathBlues));
    }

    protected void updateState() {
        agentsNumber = world.bluesAgents.size() + world.redsAgents.size();
    }

    public MainFrame getFrame() {
        return m_frame;
    }

    public World getWorld() {
        return world;
    }

    public List<AgentInTree> getAllAgents() throws KeySizeException {
        Pair<Integer,Integer> size = m_frame.getOptionsPanel().getBoardSize();
        //TODO 20 is SQUARESIZE; can actually get this info from here (using m_frame), but do we really want to?
        double[] upperKey = {size.getValue()* SquareSize.getInstance().getValue(),size.getKey()*SquareSize.getInstance().getValue()};
        double[] dk = {0,0};
        return world.getAgentsTree().range(dk,upperKey);
    }

    public void insertNewAgentToTree(double[] key, AgentInTree obs) throws KeySizeException, KeyDuplicateException {
        world.getAgentsTree().insert(key,obs);
    }
}

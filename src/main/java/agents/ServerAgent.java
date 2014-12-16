package main.java.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;
import main.java.gui.BoardPanel;
import main.java.gui.MainFrame;
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
        World.AgentType[] types = {World.AgentType.WARRIOR, World.AgentType.ARCHER, World.AgentType.COMMANDER};

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
                            || (m_frame.getOptionsPanel().getLimitButton().isSelected()
                            && (int)m_frame.getOptionsPanel().getTurnsLimitSpinner().getValue() <= stepsCounter)) {
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

                        m_frame.redrawBoard(world.getAllAgents());
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
                            if (agentsCounter == agentsNumber) {
                                agentsCounter = 0;
                                stepsCounter++;

                                m_frame.redrawBoard(world.getAllAgents());
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

        private void generateStatistics() {
            String line = "";
            for (World.AgentsSides s : sides) {
                line += s.toString() + ": ";
                for (World.AgentType t : types) {
                    line += t.toString() + ": " + world
                            .getAllAgents()
                            .parallelStream()
                            .filter(a->a.side == s && a.type == t)
                            .count() + " ";
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

    public void prepareSimulation(ArrayList<Pair<World.AgentType,Integer>> blues,
                                  ArrayList<Pair<World.AgentType,Integer>> reds,
                                  HashMap<String,ArrayList<JSONObject>> map,
                                  long timestep) {
        this.timestep = timestep;
        if (world != null) {
            world.clean();
            doWait(100);
        }
        if (map == null)
            world = new World(this,blues,reds);
        else
            world = new World(this,map);

        serverBehaviour.reset();
        m_frame.redrawBoard(world.getAllAgents());
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
}

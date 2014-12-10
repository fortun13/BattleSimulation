package main.java.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import main.java.gui.MainFrame;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    Behaviour serverBehaviour = new Behaviour() {

        int state = 0;
        int agentsCounter = 0;
        int stepsCounter = 0;

        long time;

        long interval = 300;
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
                                //m_frame.redrawBoard(world.getAgents());
                                //System.out.println("Time: " + time);
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


    // Internal implementation methods
    //////////////////////////////////

    /**
     * Setup the UI, which means creating and showing the main frame.
     */
    private void setupUI() {
        m_frame = new MainFrame(this);
    }

    public void prepareSimulation(int bluesAgentsNumber, int redsAgentsNumber) {
        if (world != null) {
            world.clean();
        }
        world = new World(this, bluesAgentsNumber, redsAgentsNumber);
        serverBehaviour.reset();

        m_frame.redrawBoard(world.getAgentsTree());

    }

    public void prepareSimulation(HashMap<String,ArrayList<JSONObject>> map, int boardWidth) {
        if (world != null)
            world.clean();
        world = new World(this,map, boardWidth);
        serverBehaviour.reset();

        m_frame.redrawBoard(world.getAgentsTree());
    }

    private World generateWorld(int bluesAgentsNumber, int redsAgentsNumber) {
        return new World(this,bluesAgentsNumber,redsAgentsNumber);
    }
    
    public void startSimulation() {
        System.out.println("Simulation started");

        //m_frame.redrawBoard(world.getAgents());

        updateState();

        addBehaviour(serverBehaviour);
    }

    protected void updateState() {
        agentsNumber = world.bluesAgents.size() + world.redsAgents.size();
    }

    public MainFrame getFrame() {
        return m_frame;
    }
}

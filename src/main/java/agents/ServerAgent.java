package main.java.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import main.java.gui.MainFrame;

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


    public ServerAgent() {
        super();
    }

    /**
     * Setup the agent.  Registers with the DF, and adds a behaviour to
     * process incoming messages.
     */
    protected void setup() {
        try {
            System.out.println( getLocalName() + " setting up");


            // create the agent descrption of itself
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName( getAID() );
            DFService.register(this, dfd);

            // add the GUI
            setupUI();
        }
        catch (Exception e) {
            System.out.println( "Saw exception in ServerAgent: " + e );
            e.printStackTrace();
        }

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
            System.out.println("clearing the world");
            world.clean();
            System.out.println("world cleared");
        }

        world = new World(this, bluesAgentsNumber, redsAgentsNumber);

        m_frame.redrawBoard2(world.getAgents2());
    }
    
    public void startSimulation() {
        System.out.println("Simulation started");

        //m_frame.redrawBoard(world.getAgents());

        ACLMessage newTurn = new ACLMessage(ACLMessage.INFORM);
        world.bluesAgents.forEach(newTurn::addReceiver);
        world.redsAgents.forEach(newTurn::addReceiver);

        newTurn.setConversationId("new-turn");

        updateState();

        addBehaviour(new Behaviour() {

            int state = 0;
            int agentsCounter = 0;
            int stepsCounter = 0;

            long time;

            long interval = 500;

            @Override
            public void action() {
                switch (state) {
                    case 0:
                        /*TODO
                            for now it's fixed number of iterations - but we will have to detect if every agent from one side is dead
                            and then stop simulation
                         */
                        if (world.redsAgents.size() == 0 || world.bluesAgents.size() == 0) {
                            state = 2;
                            System.out.println("Turn: " + stepsCounter);
                            //System.out.println("Blues: " + world.bluesAgents.size());
                            //System.out.println("Reds: " + world.redsAgents.size());
                            //System.out.println("State: " + agentsNumber);
                            ACLMessage endBattle = new ACLMessage(ACLMessage.INFORM);
                            world.redsAgents.forEach(endBattle::addReceiver);
                            world.bluesAgents.forEach(endBattle::addReceiver);
                            endBattle.setConversationId("battle-ended");
                            send(endBattle);
                            m_frame.redrawBoard2(world.getAgents2());
                            break;
                        }

                        send(newTurn);
                        time = System.currentTimeMillis();
                        state = 1;
                        System.out.println("Turn: " + stepsCounter);
                        //System.out.println("Blues: " + world.bluesAgents.size());
                        //System.out.println("Reds: " + world.redsAgents.size());
                        //System.out.println("State: " + agentsNumber);
                        break;
                    case 1:
                        ACLMessage msg = receive();
                        if (msg != null) {
                            if (msg.getConversationId().equals("ended-computation")) {
                                agentsCounter++;
                                if (agentsCounter == agentsNumber) {
                                    agentsCounter = 0;
                                    stepsCounter++;

                                    m_frame.redrawBoard2(world.getAgents2());
                                   //m_frame.redrawBoard(world.getAgents());
                                    //System.out.println("Time: " + time);
                                    while (System.currentTimeMillis() - time < interval)
                                        block(interval - (System.currentTimeMillis() - time));

                                    state = 0;
                                    break;
                                }
                            } else if (msg.getConversationId().equals("agent-dead")) {
                                //AID sender = msg.getSender();
                                //newTurn.removeReceiver(msg.getSender());
                                /*if (world.bluesAgents.contains(sender)) {
                                    world.bluesAgents.remove(sender);
                                    newTurn.removeReceiver(sender);
                                } else if (world.redsAgents.contains(sender)) {
                                    world.redsAgents.remove(sender);
                                    newTurn.removeReceiver(sender);
                                }*/
                                //updateState();

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
        });

    }

    protected void updateState() {
        agentsNumber = world.bluesAgents.size() + world.redsAgents.size();
    }

    public MainFrame getFrame() {
        return m_frame;
    }
}

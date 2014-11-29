package main.java.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import main.java.gui.MainFrame;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class ServerAgent extends Agent {

    // Instance variables
    //////////////////////////////////
    protected MainFrame m_frame = null;


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
    
    public void startSimulation(int bluesAgentsNumber, int redsAgentsNumber) {
    	System.out.println("Simulation started");

        World world = new World(this, bluesAgentsNumber, redsAgentsNumber);

        m_frame.redrawBoard(world.getAgents());

        ArrayList<AID> allAgents = new ArrayList<>();
        allAgents.addAll(world.bluesAgents);
        allAgents.addAll(world.redsAgents);

        Collections.shuffle(allAgents);

        ACLMessage newTurn = new ACLMessage(ACLMessage.INFORM);
        newTurn.setConversationId("new-turn");
        allAgents.forEach(newTurn::addReceiver);


        addBehaviour(new Behaviour() {

            int state = 0;
            int agentsCounter=0;
            int stepsCounter=0;
            int agentsNumber = allAgents.size();

            long time;

            @Override
            public void action() {
                switch (state) {
                    case 0:
                        /*TODO
                            for now it's fixed number of iterations - but we will have to detect if every agent from one side is dead
                            and then stop simulation
                         */
                        if (stepsCounter == 20) {
                            state = 2;
                            break;
                        }
                        send(newTurn);
                        time = System.currentTimeMillis();
                        state++;
                        break;
                    case 1:
                        ACLMessage msg = receive();
                        if (msg != null) {
                            if (msg.getConversationId().equals("ended-computation")) {
                                agentsCounter++;
                                if (agentsCounter == agentsNumber) {
                                    agentsCounter = 0;
                                    stepsCounter++;
                                    state--;
                                    if (System.currentTimeMillis()-time < time)
                                        block(1000-(System.currentTimeMillis()-time));
                                    m_frame.redrawBoard(world.getAgents());
                                    break;
                                }
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

}

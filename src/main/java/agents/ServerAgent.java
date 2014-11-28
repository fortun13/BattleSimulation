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

            // add a Behaviour to handle messages from guests
                /*addBehaviour( new CyclicBehaviour( this ) {
                    public void action() {
                        ACLMessage msg = receive();

                        if (msg != null) {
                            if (HELLO.equals( msg.getContent() )) {
                                // a guest has arrived
                                m_guestCount++;
                                setPartyState( "Inviting guests (" + m_guestCount + " have arrived)" );

                                if (m_guestCount == m_guestList.size()) {
                                    System.out.println( "All guests have arrived, starting conversation" );
                                    // all guests have arrived
                                    beginConversation();
                                }
                            }
                            else if (RUMOUR.equals( msg.getContent() )) {
                                // count the agents who have heard the rumour
                                incrementRumourCount();
                            }
                            else if (msg.getPerformative() == ACLMessage.REQUEST  &&  INTRODUCE.equals( msg.getContent() )) {
                                // an agent has requested an introduction
                                doIntroduction( msg.getSender() );
                            }
                        }
                        else {
                            // if no message is arrived, block the behaviour
                            block();
                        }
                    }
                } );*/
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

        ArrayList<AID> allAgents = new ArrayList<>();
        allAgents.addAll(world.bluesAgents);
        allAgents.addAll(world.redsAgents);

        Collections.shuffle(allAgents);

        //while (!world.bluesAgents.isEmpty() || !world.redsAgents.isEmpty()) {
        ACLMessage newTurn = new ACLMessage(ACLMessage.INFORM);
        newTurn.setConversationId("new-turn");
        for(AID agent : allAgents) {
            newTurn.addReceiver(agent);
        }


        addBehaviour(new Behaviour() {

            int state = 0;
            int agentsCounter=0;
            int stepsCounter=0;
            int agentsNumber = allAgents.size();

            @Override
            public void action() {
                switch (state) {
                    case 0:
                        if (stepsCounter == 20) {
                            state = 2;
                            break;
                        }
                        send(newTurn);
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

        /*for (int i=0;i<20;i++) {

            send(newTurn);

            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            //for now - slowing down program with empty loop
            /*for (int j=0;j<100000;j++) {

            }*/
            //System.out.println("newturn");
            /*try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        //}

        /*ACLMessage end = new ACLMessage(ACLMessage.INFORM);
        end.setConversationId("battle-ended");
        for (AID agent : allAgents)
            end.addReceiver(agent);

        send(end);*/

    }

}

package main.java.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import main.java.gui.MainFrame;

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

}

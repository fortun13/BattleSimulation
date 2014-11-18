package main.java.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.util.Pair;

import java.awt.geom.Point2D;

/**
 * Created by Jakub Fortunka on 18.11.14.
 */
public class CannonFodder extends Agent {

    private int condition, strength, speed, accuracy;

    private int x,y;

    private AID world;

    //private MessageTemplate mt;

    protected void setup(Behaviour b) {

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription service = new ServiceDescription();
        service.setType("world");
        template.addServices(service);
        try {
            DFAgentDescription[] result = DFService.search(this,template);
            world = result[0].getName();

        } catch (FIPAException e) {
            //TODO
            //handle exception
            e.printStackTrace();
        }


        condition = 40;
        strength = 10;
        speed = 3;
        accuracy = 90;
        addBehaviour(b);
    }


    protected void takeDown() {

    }

    protected Agent getNearestEnemy() {
        // TODO
        // have to have representation of environment to do something with it
        return null;
    }

    protected void gotoEnemy(Agent enemy) {
        //Can be changed - it's just for visualization so don't care about Pair or something
        //Pair<Point2D,Point2D> localization = enemy.getCurrentPosition();

        // not exactly sure how to compute where agent should go

        //Pair<Point2D,Point2D> destination = computeDestination(enemy.getCurrentPosition());

        // CFP - i'm assuming that (maybe) agent should wait for confirmation if he can really go to destination point
        //ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        //msg.addReceiver(world);

        /*

        Actually don't know if this "negotation" should be here or in behaviour class

         */
        //TODO
        //do something bettern than .toString()
        /*msg.setContent(destination.toString());
        msg.setConversationId("destination-propose");
        msg.setReplyWith("destination-propose"+System.currentTimeMillis());
        send(msg);
        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("destination-purpose"), MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

        ACLMessage rpl = receive(mt);
        if (rpl != null) {
            if (rpl.getContent().equals("good")) {

            }
        }*/

        //Nevermind - assuming i have method - i leave negotation commented in case it would be needed

        Pair<Point2D,Point2D> destination;

        do {
            destination = computeDestination(enemy.getCurrentPosition());
        } while (!world.moveAgent(this,destination));

    }

    protected void attack(Agent enemy) {
        world.attack(this,enemy);
    }


    public int getCondition() {
        return condition;
    }
}

package main.java.agents;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;
import java.lang.Math.*;

/**
 * Created by Jakub Fortunka on 18.11.14.
 *
 */
public class CannonFodder extends AgentWithPosition {

    private int condition, strength, speed, accuracy;

    private World world;

    private World.AgentsSides agentSide;

    //private MessageTemplate mt;

    protected void setup() {

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription service = new ServiceDescription();
        service.setType("world");
        template.addServices(service);
        try {
            DFAgentDescription[] result = DFService.search(this, template);


            //world = result[0].getName();

        } catch (FIPAException e) {
            //TODO
            //handle exception
            e.printStackTrace();
        }

        //TODO is it going to work?

        // 0 - behaviour
        // 1 - condition
        // 2 - strength
        // 3 - speed
        // 4 - accuracy
        // 5 - world

        Object[] parameters = getArguments();

        addBehaviour((ReactiveBehaviour) parameters[0]);
        this.condition = (int) parameters[1];
        this.strength = (int) parameters[2];
        this.speed = (int) parameters[3];
        this.accuracy  = (int) parameters[4];
        this.agentSide = (World.AgentsSides) parameters[5];
        this.world = (World) parameters[6];
        this.position = (World.AgentInTree) parameters[7];

        setEnabledO2ACommunication(true,10);
    }


    protected void takeDown() {

    }

    protected CannonFodder getNearestEnemy() {
        //TODO
        //have to have representation of environment to do something with it
        return world.getNearestEnemy(this);
    }

    //protected void gotoEnemy(CannonFodder enemy) {
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

        //Pair<Point2D,Point2D> destination;
        //Point2D destination;

//        do {
//            destination = computeDestination(enemy.getCurrentPosition());
//            // I assume that method moveAgent is returning boolean - if agent can be move to that position, do it and return true
//        } while (!world.moveAgent(this,destination));

    //}

    protected void gotoEnemy(AgentWithPosition enemy) {
        // I don't really know...
        // I mean - here should be computed some kind of "vector" in which we will be travelling
        // If agent is in range of other agent - then set destination to closest free point
        // If not - then approach other agent as fast as possible (using computed vector)

        // Vector between agent and spotted enemy

        Point2D thisPosition = position.pos();
        Point2D enemyPosition = enemy.getPosition().pos();
        int vec[] = new int[] {(int)thisPosition.getX() - (int)enemyPosition.getX(),
                                (int)thisPosition.getY() - (int)enemyPosition.getY()};
        /* Compute movement vector, for example: [4, -7] => [4/|4|, -7/|-7|] => [1, -1] */
        vec[0] = vec[0]/Math.abs(vec[0]);
        vec[1] = vec[1]/Math.abs(vec[1]);
        Point2D destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getX() + vec[1]);
        // Check if agent can move in skew vector (Po ukośnym wektorze :D)
        if(!world.moveAgent(this,destination)) {
            // Check if agent can move horizontally
            destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getX());
            if (!world.moveAgent(this,destination)) {
                // Check if agent can move vertically
                destination = new Point2D(thisPosition.getX(), thisPosition.getX() + vec[1]);
                if(!world.moveAgent(this,destination)) {
                    //TODO should we program this situation?
                }
            }
        }
    }

    public boolean enemyInRangeOfAttack(AgentWithPosition enemy) {
        //for now - assuming that speed also means how far agent can go in one turn
        // i guess 1 is one square - so it will return true if agent is standing right beside enemy
        // have to be overridden for all agents that can attack from distance
        // TODO
        if (world.getPosition(this).distance(world.getPosition(enemy)) < 2)
            return true;
        else
            return false;
    }

    protected void attack(CannonFodder enemy) {
        world.attack(this,enemy);
    }


    public int getCondition() {
        return condition;
    }

    public int setCondition(int condition) {
        this.condition = condition;
        return 1;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public World.AgentsSides getAgentSide() {
        return agentSide;
    }

    public void setAgentSide(World.AgentsSides agentSide) {
        this.agentSide = agentSide;
    }


}

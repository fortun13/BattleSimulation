package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import javafx.geometry.Point2D;
import main.java.gui.BoidOptions;
import main.java.utils.AgentBuilder;
import main.java.utils.AgentInTree;
import main.java.utils.Prototype;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jakub Fortunka on 20.11.14.
 *
 */
public abstract class AgentWithPosition extends Agent {
    protected int fieldOfView = 200;

    protected double previousRatio=1;

    protected double psychologicalResistance = 0.7;

    protected World world;

    protected AgentInTree currentState;

    protected AID commander;
    
    protected Statistics stats;

    public void setup() {
        Object[] parameters = getArguments();

        Behaviour behaviour = (Behaviour) parameters[AgentBuilder.BEHAVIOUR];
        addBehaviour    (behaviour);
        stats =         (Statistics) parameters[AgentBuilder.STATS];
        world =              (World) parameters[AgentBuilder.WORLD];
        currentState = (AgentInTree) parameters[AgentBuilder.POSITION];
    }

    /**
     * Method checks if enemy is in range of attack of agent
     *
     * @param enemy Object representing enemy
     * @return true if agent can attack enemy
     */
    public boolean enemyInRangeOfAttack(AgentInTree enemy) {
        return currentState.pos().distance(enemy.pos()) < stats.attackRange;
    }

    /**
     * method returning nearest enemy (if it's possible - if any enemies are in field of view of agent)
     *
     * @return AgentInTree object representing agent; null if no enemy was found within field of view of agent
     */
    protected AgentInTree getNearestEnemy() {
        return world.getNearestEnemy(this);
    }

    /**
     * Method which is responsible for the way in which agent will reach his enemy
     *
     * @param enemy Object of AgentInTree representing enemy agent
     */
    protected void gotoEnemy(AgentInTree enemy) {
        goToPoint(enemy.p);
    }

    /**
     *
     *
     * @return current state of agent - his position and some other elements (look to {@link main.java.utils.AgentInTree})
     */
    public AgentInTree getCurrentState() {
        return currentState;
    }

    /**
     * Method specifies how agent will react for attack of enemy
     *
     * @param msg message from enemy with his parameters
     */
    public abstract void reactToAttack(ACLMessage msg);

    /**
     * method checks if agent is motivated for fight
     *
     * @return true if agent is motivated
     */
    public boolean isMotivated() {
        int [] count;
        count = world.countFriendFoe(this);
        //System.out.println("Friends: " + count[0] + " Enemies: " + count[1]);
        if (currentState.morale > 50)
            return true;

        if (count[1] == 0) {
            currentState.morale += 4;
            return currentState.morale > 0;
        } else if (count[0] == 0) {
            currentState.morale -= 4;
            return currentState.morale > 0;
        }
        double ratio = ((double)count[0])/((double)count[1]);
        //System.out.println("Ratio: " + ratio);
        if (ratio < psychologicalResistance && ratio < previousRatio)
            currentState.morale -= (1/ratio +2);
        if (ratio >= 1 && currentState.morale<50)
            currentState.morale += ratio;
        previousRatio = ratio;
        //System.out.println(getLocalName() + " Morale: " + morale);
        return currentState.morale > 0;
    }

    /**
     * sends message to enemy about his death
     * @param msgToSend message to enemy
     */
    protected void sendMessageToEnemy(ACLMessage msgToSend) {
        msgToSend.setConversationId("enemy-dead");
        send(msgToSend);
    }

    /**
     * specifies what agent should do after his death
     * @param msgToSend kj
     */
    protected void killYourself(ACLMessage msgToSend) {
        System.out.println("I'm dead :( " + getLocalName());
        sendMessageToEnemy(msgToSend);
        world.killAgent(this);
    }

    protected Point2D gesDestination() {
        Point2D pos = currentState.pos();
        double[] s = getSpeedHV();
        return new Point2D(pos.getX() + s[0], pos.getY() + s[1]);
    }

    /**
     * method responsible for managing the attack on enemy
     *
     * @param enemy AID of enemy
     * @param currentState current state of agent (position etc.)
     */
    protected void attack(AID enemy, AgentInTree currentState) {
        if (Math.random() * 100 <= stats.accuracy) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("attack");
            String msgContent = currentState.condition + ":" + stats.strength + ":" + stats.speed + ":" + stats.accuracy;
            msg.setContent(msgContent);
            msg.addReplyTo(getAID());
            msg.addReceiver(enemy);
            msg.setSender(getAID());
            send(msg);
        }
    }

    /**
     * method responsible for moving agent from his current position to new position (at least to change position as close to new position as possible)
     * @param pos desired position
     */
    public void goToPoint(Point2D pos) {
        Point2D mp = currentState.pos();

        BoidOptions options = world.server.getFrame().getOptionsPanel().options;
        setSpeedHV(pos.getX() - mp.getX(), pos.getY() - mp.getY(), options.getAgentSize());

        final double[] key = {mp.getX(), mp.getY()};
        double angle = currentState.getAngle();
        double speed = currentState.getSpeed();
        try {
            // agent szuka sąsiadów
            // zakres widzenia jest kwadratem, zeby nie liczyc niepotrzenie pierwiastka w obliczeniach ponizej
            double fieldOfView = options.getRangeOfView();
            final double rayOfView = fieldOfView*fieldOfView;
            final double angleOfView = Math.toRadians(options.getAngleOfView() / 2); // po pol na strone
            final double finalAngle = angle;
            List<AgentInTree> neighbours = world.getAgentsTree().nearest(key, 20, agentInTree -> agentInTree.side == currentState.side).parallelStream().filter(agentInTree -> {
                Point2D p2 = agentInTree.pos();
                if (p2 != mp) if (sqrDst(mp, p2) < rayOfView)
                    if (Math.abs(Math.atan2(p2.getY() - mp.getY(), p2.getX() - mp.getX()) - finalAngle) < angleOfView)
                        return true;
                return false;
            }).collect(Collectors.toList());

            // agent stara się nie wychodzić przed szereg:
            final double temporize = options.getTemporize();
            if (neighbours.size() == 0)
                speed *= temporize + Math.random()*(1-temporize);
            setSpeedVector(angle, speed);

            // agent dostosowuje prędkość i kierunek do innych
            final double folowingWeight = options.getFollowingWeight();
            double avgAngle = neighbours.parallelStream().mapToDouble(AgentInTree::getAngle).average().orElse(angle);
            double avgSpeed = neighbours.parallelStream().mapToDouble(AgentInTree::getSpeed).average().orElse(speed);

            angle = angle + folowingWeight * (avgAngle - angle);
            speed = speed + folowingWeight*(avgSpeed - speed);
            setSpeedVector(angle, speed);

            // agent stara się być w środku grupy
            final double seekCenterWeight = options.getSeekCenterWeight();
            double[] HVSpeed = getSpeedHV().clone();
            double meanDst = Math.sqrt(neighbours.parallelStream().mapToDouble(a -> sqrDst(mp, a.pos())).average().orElse(0));
            neighbours.forEach(n -> {
                double dst = sqrDst(mp, n.pos());
                dst = Math.sqrt(dst);
                HVSpeed[0] += seekCenterWeight * (n.pos().getX() - mp.getX()) * (dst - meanDst) / dst;
                HVSpeed[1] += seekCenterWeight * (n.pos().getY() - mp.getY()) * (dst - meanDst) / dst;
            });
            setSpeedHV(HVSpeed[0], HVSpeed[1]);

            // utrzymanie minimalnej dległości od wszystkiego oprócz obranego celu
            double min = options.getMinimalDistance();
            List<AgentInTree> anything = world.getAgentsTree().nearest(key, 20, ait -> ait.p != pos).parallelStream()
                    .filter(agentInTree -> {
                        Point2D p2 = agentInTree.pos();
                        if (p2 != mp) if (sqrDst(mp, p2) < 3*min)
//                            if (Math.abs(Math.atan2(p2.getY() - mp.getY(), p2.getX() - mp.getX()) - finalAngle) < angleOfView)
                            return true;
                        return false;
                    }).collect(Collectors.toList());
            final double avoidWeight = options.getAvoidingWeight();
            double[] HVSpeed2 = getSpeedHV().clone();
            anything.forEach(thing -> {
                double dst = Math.sqrt(sqrDst(mp, thing.pos()));
                double xDst = thing.pos().getX() - mp.getX();
                double yDst = thing.pos().getY() - mp.getY();
                HVSpeed2[0] -= avoidWeight * (xDst * min / dst - xDst);
                HVSpeed2[1] -= avoidWeight * (yDst * min / dst - yDst);
            });
            setSpeedHV(HVSpeed2[0], HVSpeed2[1]);

//            while (!world.moveAgent(this, gesDestination())) {
//                setSpeedVector(Math.random()*360, speed);
//            }
            // jeśli ruch jest nie dozwolony przez świat, to agent się zatrzymuje
            if (!world.moveAgent(this, gesDestination())) {
                setSpeedVector(angle, 0);
            }
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

    }

    private double sqrDst(Point2D mp, Point2D p2) {
        return (p2.getX() - mp.getX()) * (p2.getX() - mp.getX()) + (p2.getY() - mp.getY()) * (p2.getY() - mp.getY());
    }

//
//    public void setSpeedVector(double angle, double radius) {
//        setSpeedVector(angle, Math.min(radius, speed));
//    }

    public void setSpeedVector(double angle, double radius) {
        currentState.speed[0] = angle;
        currentState.speed[1] = Math.min(radius, stats.speed);
    }

    public double[] getSpeedHV() {
        double angle = currentState.speed[0], r = currentState.speed[1];
        return new double[]{r * Math.cos(angle), r * Math.sin(angle)};
    }

    public void setSpeedHV(double hSpeed, double vSpeed) {
        setSpeedVector(Math.atan2(vSpeed, hSpeed), Math.sqrt(hSpeed*hSpeed + vSpeed*vSpeed));
    }

    public void setSpeedHV(double hSpeed, double vSpeed, double limit) {
        setSpeedVector(Math.atan2(vSpeed, hSpeed), Math.sqrt(hSpeed*hSpeed + vSpeed*vSpeed) - limit);
    }

    public AID getCommander() {
        return commander;
    }
    public void setCommander(AID commander) {
        this.commander = commander;
    }

    public static class Statistics implements Cloneable, Prototype {
        public Statistics(int strength, int accuracy, int speed, int attackRange) {
            this.accuracy = accuracy;
            this.attackRange = attackRange;
            this.speed = speed;
            this.strength = strength;
        }
        public Integer strength = 0, speed = 0, accuracy = 0, attackRange = 0;

        public Statistics() { }

        @Override
        public Statistics clone() throws CloneNotSupportedException {
            super.clone();
            return new Statistics(strength, accuracy, speed, attackRange);
        }

        @Override
        public void setup(List list) {
            strength    = (Integer) list.get(0);
            speed       = (Integer) list.get(1);
            accuracy    = (Integer) list.get(2);
            attackRange = (Integer) list.get(3);
        }
    }
}

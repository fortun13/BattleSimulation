package main.java.agents;

import edu.wlu.cs.levy.CG.KeySizeException;
import jade.core.AID;
import javafx.geometry.Point2D;
import main.java.utils.AgentInTree;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jakub Fortunka on 18.11.14.
 *
 */
public abstract class CannonFodder extends AgentWithPosition {

    protected int strength, speed, accuracy, attackRange;

    public void setup() {
        // 0 - behaviour
        // 1 - condition
        // 2 - strength
        // 3 - speed
        // 4 - accuracy
        // 5 - world
        // 6 - position
        // 7 - attack range

        Object[] parameters = getArguments();

        addBehaviour((ReactiveBehaviour) parameters[0]);
        this.strength = (int) parameters[2];
        this.speed = (int) parameters[3];
        this.accuracy = (int) parameters[4];
        this.world = (World) parameters[5];
        this.position = (AgentInTree) parameters[6];
        this.position.condition = (int) parameters[1];
        this.attackRange = (int) parameters[7];
    }

    @Override
    public void doDelete() {
        world.removeAgent(this);
        super.doDelete();
    }

    @Override
    protected AgentInTree getNearestEnemy() {
        return world.getNearestEnemy(this);
    }

    @Override
    protected void gotoEnemy(AgentInTree enemy) {
        goToPoint(enemy.p);
    }

    private void goToPoint(Point2D ep) {
        Point2D mp = position.pos();

        double size = (Integer) world.server.getFrame().getOptionsPanel().as.getValue();
        setSpeedHV(ep.getX() - mp.getX(), ep.getY() - mp.getY(), size);

        final double[] key = {mp.getX(), mp.getY()};
        double angle = position.getAngle();
        double speed = position.getSpeed();
        try {
            // agent szuka sąsiadów
            // zakres widzenia jest kwadratem, zeby nie liczyc niepotrzenie pierwiastka w obliczeniach ponizej
            final double rayOfView = fieldOfView*fieldOfView;
            final double angleOfView = Math.toRadians(200 / 2); // po pol na strone
            final double finalAngle = angle;
            List<AgentInTree> neighbours = world.getAgentsTree().nearest(key, 20, agentInTree -> agentInTree.side == position.side).parallelStream().filter(agentInTree -> {
                Point2D p2 = agentInTree.pos();
                if (p2 != mp) if (sqrDst(mp, p2) < rayOfView)
                    if (Math.abs(Math.atan2(p2.getY() - mp.getY(), p2.getX() - mp.getX()) - finalAngle) < angleOfView)
                        return true;
                return false;
            }).collect(Collectors.toList());

            // agent stara się nie wychodzić przed szereg:
            final double temporize = 0.4;
            if (neighbours.size() == 0)
                speed *= temporize + Math.random()*(1-temporize);
            setSpeedVector(angle, speed);

            // agent dostosowuje prędkość i kierunek do innych
            final double weight = 0.1;
            double avgAngle = neighbours.parallelStream().mapToDouble(AgentInTree::getAngle).average().orElse(angle);
            double avgSpeed = neighbours.parallelStream().mapToDouble(AgentInTree::getSpeed).average().orElse(speed);

            angle = angle + weight * (avgAngle - angle);
            speed = speed + weight*(avgSpeed - speed);
            setSpeedVector(angle, speed);

            // agent stara się być w środku grupy
            final double COMweight = 0.1;
            double[] HVSpeed = getSpeedHV().clone();
            double meanDst = Math.sqrt(neighbours.parallelStream().mapToDouble(a -> sqrDst(mp, a.pos())).average().orElse(0));
            neighbours.forEach(n -> {
                double dst = sqrDst(mp, n.pos());
                dst = Math.sqrt(dst);
                HVSpeed[0] += COMweight * (n.pos().getX() - mp.getX()) * (dst - meanDst) / dst;
                HVSpeed[1] += COMweight * (n.pos().getY() - mp.getY()) * (dst - meanDst) / dst;
            });
            setSpeedHV(HVSpeed[0], HVSpeed[1]);

            // utrzymanie minimalnej dległości od wszystkiego oprócz obranego celu
            double min = 30;
            List<AgentInTree> anything = world.getAgentsTree().nearest(key, 20, ait -> ait.p != ep).parallelStream()
                    .filter(agentInTree -> {
                        Point2D p2 = agentInTree.pos();
                        if (p2 != mp) if (sqrDst(mp, p2) < 3*min)
//                            if (Math.abs(Math.atan2(p2.getY() - mp.getY(), p2.getX() - mp.getX()) - finalAngle) < angleOfView)
                            return true;
                        return false;
                    }).collect(Collectors.toList());
            final double avoidWeight = 0.1;
            double[] HVSpeed2 = getSpeedHV().clone();
            anything.forEach(thing -> {
                double dst = Math.sqrt(sqrDst(mp, thing.pos()));
                double xDst = thing.pos().getX() - mp.getX();
                double yDst = thing.pos().getY() - mp.getY();
                HVSpeed2[0] += avoidWeight * (xDst * min / dst - xDst);
                HVSpeed2[1] += avoidWeight * (yDst * min / dst - yDst);
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

    protected void followAgent(AgentInTree leader) {
        Point2D mp = position.pos();
        Point2D ep = leader.pos();

        double size = (Integer) world.server.getFrame().getOptionsPanel().as.getValue();
        setSpeedHV(ep.getX() - mp.getX(), ep.getY() - mp.getY(), size);

        final double[] key = {mp.getX(), mp.getY()};
        double angle = position.getAngle();
        double speed = position.getSpeed();

        // agent stara się nie wychodzić przed szereg:
        final double temporize = 0.4;
        speed *= temporize + Math.random()*(1-temporize);
        setSpeedVector(angle, speed);

        // agent dostosowuje prędkość i kierunek do innych
        angle = leader.getAngle();
        speed = leader.getSpeed();
        setSpeedVector(angle, speed);
        if (!world.moveAgent(this, gesDestination())) {
            setSpeedVector(angle, 0);
        }
    }

    private double sqrDst(Point2D mp, Point2D p2) {
        return (p2.getX() - mp.getX()) * (p2.getX() - mp.getX()) + (p2.getY() - mp.getY()) * (p2.getY() - mp.getY());
    }

    @Override
    protected void keepPosition() {
        /*List<AgentInTree> friendlyNeighbors;

        friendlyNeighbors = world.getNeighborFriends(this);

        Point2D thisPosition = position.pos();
        double vec[] = {0, 0};
        double posX, posY, srDistance = 0, pomDistance;
        for (AgentInTree friendlyNeighbor : friendlyNeighbors) {
            posX = friendlyNeighbor.pos().getX();
            posY = friendlyNeighbor.pos().getY();
            srDistance = srDistance + Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
        }
        for (AgentInTree friendlyNeighbor : friendlyNeighbors) {
            posX = friendlyNeighbor.pos().getX();
            posY = friendlyNeighbor.pos().getY();
            pomDistance = Math.sqrt(Math.pow(posX - thisPosition.getX(), 2) + Math.pow(posY - thisPosition.getY(), 2));
            vec[0] = vec[0] + ((posX - thisPosition.getX()) * (pomDistance - srDistance)) / pomDistance;
            vec[1] = vec[1] + ((posY - thisPosition.getY()) * (pomDistance - srDistance)) / pomDistance;
        }
        vec[0] = Math.round(vec[0]);
        vec[1] = Math.round(vec[1]);
        if (vec[0] != 0) vec[0] = vec[0]/Math.abs(vec[0]);
        if (vec[1] != 0) vec[1] = vec[1]/Math.abs(vec[1]);
        // Just move your ass...
        if(vec[0] == 0.0 && vec[1] == 0.0){
            vec[0] = world.computeBoardCenter(this.position.pos());
        }
        Point2D destination = new Point2D(thisPosition.getX() + vec[0], thisPosition.getY() + vec[1]);
        world.moveAgent(this,destination);*/

        //TODO there sould be world.boardCenterY, so agent would go to the center of the board
        goToPoint(new Point2D(world.boardCenterX,world.boardCenterX));
    }

    protected abstract void attack(AID enemy, AgentInTree position);

    @Override
    public void setSpeedVector(double angle, double radius) {
        super.setSpeedVector(angle, Math.min(radius, speed));
    }
}

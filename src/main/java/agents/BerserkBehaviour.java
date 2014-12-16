package main.java.agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by Jakub Fortunka on 18.11.14.
 * Behaviour presented in a frenzy of battle
 */
public class BerserkBehaviour extends ReactiveBehaviour {
    private static final int FOLLOWIN = 1;
    private static final int BORED = 0;

    @Override
    public void handleMessage(ACLMessage msg) {
        switch (msg.getConversationId()) {
            case "commander-init":
                //TODO probably will have to check if this turn message was send to server (boolean?)
                myAgent.removeBehaviour(new BerserkBehaviour());
                myAgent.addBehaviour(new CommanderMinionBehaviour());
                ((CannonFodder)myAgent).setCommander(msg.getSender());
                state = 2;
                break;
        }
    }

    @Override
    public void decideOnNextStep() {
        CannonFodder agent = (CannonFodder) myAgent;
        if (agent.position.condition <= 0) {
            return ;
        }
        if (enemyPosition == null || enemyPosition.isDead) {
            state = BORED;
        }
        switch(state) {
            case BORED:
                enemyPosition = agent.getNearestEnemy();
                if (enemyPosition != null) {
                    enemy = new AID(enemyPosition.getAgentName(),true);
                    agent.gotoEnemy(enemyPosition);
                    state = FOLLOWIN;
                }
                else {
                    ((CannonFodder) myAgent).goToPoint(((CannonFodder) myAgent).world.returnBoardCenter());
                }
                break;
            case FOLLOWIN:
                if (agent.enemyInRangeOfAttack(enemyPosition)) {
                    /*try {
                        Clip c = AudioSystem.getClip();

                        File stream = new File("res/" + (Math.random() > 0.5 ? "one_" : "") + "shot0.wav");
                        c.open(AudioSystem.getAudioInputStream(stream));
//                        c.addLineListener(event -> {
//                            if (event.getType().equals(LineEvent.Type.STOP))
//                            {
//                                Line soundClip = event.getLine();
//                                soundClip.close();
//                            }
//                        });
                        c.start();
                    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                        System.out.println("nie będzie muzyki");
                    }*/

                    agent.setSpeedVector(0, 0);
                    agent.attack(enemy, enemyPosition);
                } else {
                    agent.gotoEnemy(enemyPosition);

                    //if (Math.random() > 0.001) break;
                /*try {
                        Clip c = AudioSystem.getClip();

                        File stream = new File("res/cast.wav");
                        c.open(AudioSystem.getAudioInputStream(stream));
//                        c.addLineListener(event -> {
//                            if (event.getType().equals(LineEvent.Type.STOP))
//                            {
//                                Line soundClip = event.getLine();
//                                soundClip.close();
//                            }
//                        });
                        c.start();
                    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                        System.out.println("nie będzie muzyki");
                    }*/
                }
                break;
        }
    }
}

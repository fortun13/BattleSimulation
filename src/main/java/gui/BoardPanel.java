package main.java.gui;

//import javafx.geometry.Point2D;

import javafx.geometry.Point2D;
import javafx.util.Pair;
import main.java.agents.World;
import main.java.utils.AgentInTree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jakub Fortunka on 08.11.14.
 *
 */
public class BoardPanel extends JPanel {

    public final int SQUARESIZE = 20;
    public Cursor cursor;
    public AffineTransform at = new AffineTransform();
    private ArrayList<Pair<World.AgentType,BufferedImage>> images = new ArrayList<>();
    public int x1, y1, x2, y2;
    public MyAgent selectedAgent = null;

	public JPanel innerBoard;

    private ArrayList<MyAgent> agentsList = new ArrayList<>();

    public BoardPanel() {
        super();
        setBackground(Color.WHITE);

        int WIDTH = 700;
        int HEIGHT = 400;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        innerBoard = new Board();
    }

    public void generateBoard(int height, int width) {
    	//setPreferredSize(new Dimension(WIDTH,HEIGHT));
    	//innerBoard.removeAll();

        at = new AffineTransform();
        //at.scale(0.19, 0.19);

        at.scale(1,1);

    	setPreferredSize(new Dimension(width*(SQUARESIZE)+10, height*(SQUARESIZE)+10));

        innerBoard.setPreferredSize(new Dimension(width*(SQUARESIZE)+1, height*(SQUARESIZE)+1));
        add(innerBoard);

        innerBoard.revalidate();
        innerBoard.repaint();
    }

    public void resetScale() {
        at = new AffineTransform();
        at.scale(1,1);
        innerBoard.revalidate();
        innerBoard.repaint();
    }
    
    public void drawAgents(java.util.List<AgentInTree> agents) {
        //innerBoard.removeAll();
        agentsList.clear();
        for (AgentInTree agent : agents) {
            Color c;
                if (agent.side == World.AgentsSides.Blues) {
                    if (agent.isDead)
                        c = new Color(0, 4, 78);
                    else
                        c = new Color(4, 3, 228);
                }
                else {
                    if (agent.isDead) {
                        c = new Color(75, 0, 0);
                    }
                    else
                        c = new Color(221, 3, 0);
                }
            agentsList.add(new MyAgent(c,agent));
        }

        innerBoard.revalidate();
        innerBoard.repaint();
    }

    public ArrayList<MyAgent> getMyAgents() {
        return agentsList;
    }

    public class Board extends JPanel {
        @Override
        public void paint(Graphics g) {

            super.paint(g);

            for(MyAgent s : agentsList) {
                s.paint(g);
            }

            if (cursor != null)
                setCursor(cursor);

        }
    }

    public class MyAgent extends JComponent {
        private Color c;
        private AgentInTree agent;

        private Point2D pointBuffer = null;

        public MyAgent(Color c, AgentInTree agent) {
            this.c = c;

            this.agent = agent;

        }

        public AgentInTree getAgent() {
            return agent;
        }

        public Point2D getPoint() {
            return pointBuffer==null ? agent.p : pointBuffer;
        }

        public void setPoint(Point2D point) {
            pointBuffer = point;
            //pointOnBoard = point;
        }

        public void paint(Graphics g) {

            Graphics2D g2d = (Graphics2D)g;

            AffineTransform old = g2d.getTransform();
            g2d.transform(at);
            g2d.setColor(this.c);

            //g2d.fillOval((int)pointOnBoard.getX(),(int)pointOnBoard.getY(),agent.type.getSize(),agent.type.getSize());

            try {
                BufferedImage image;
                if (!images.stream().anyMatch(p -> p.getKey().equals(agent.type))) {
                    image = ImageIO.read(new File(agent.type.getValue()));
                    images.add(new Pair<>(agent.type,image));
                } else {
                    image = images.stream().filter( p -> p.getKey().equals(agent.type)).findFirst().get().getValue();
                }
                if (pointBuffer == null) {
                    //System.out.println("Position: " + agent.p);
                    g2d.fillOval((int) agent.p.getX(), (int) agent.p.getY(), agent.type.getSize(), agent.type.getSize());
                    g2d.drawImage(image, (int) agent.p.getX(), (int) agent.p.getY(), null);
                } else {
                    g2d.fillOval((int) pointBuffer.getX(), (int) pointBuffer.getY(), agent.type.getSize(), agent.type.getSize());
                    g2d.drawImage(image, (int) pointBuffer.getX(), (int) pointBuffer.getY(), null);
                }
                //g2d.drawImage(image,(int)agent.p.getX()*SQUARESIZE,(int)agent.p.getY()*SQUARESIZE,null);
                //g2d.drawImage(image,(int)pointOnBoard.getX(),(int)pointOnBoard.getY(),null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            g2d.setTransform(old);
        }

    }

} 

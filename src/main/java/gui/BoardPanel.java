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
    public MyAgent clickedAgent = null;

	public JPanel innerBoard;

    private ArrayList<MyAgent> agentsList = new ArrayList<>();

    public BoardPanel() {
        super();
        setBackground(Color.WHITE);

        int WIDTH = 800;
        int HEIGHT = 400;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        innerBoard = new Board();

        at.scale(0.2,0.2);
    }

    public void generateBoard(int height, int width) {
    	//setPreferredSize(new Dimension(WIDTH,HEIGHT));
    	innerBoard.removeAll();

        at = new AffineTransform();
        at.scale(0.19, 0.19);

    	setPreferredSize(new Dimension(width*(SQUARESIZE)+10, height*(SQUARESIZE)+10));

        innerBoard.setPreferredSize(new Dimension(width*(SQUARESIZE)+1, height*(SQUARESIZE)+1));
        add(innerBoard);

        innerBoard.revalidate();
        innerBoard.repaint();
    }
    
    public void drawAgents(java.util.List<AgentInTree> agents) {
        innerBoard.removeAll();
        agentsList.clear();
        for (AgentInTree agent : agents) {
            Color c;
            Point2D p = agent.pos();

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
            agentsList.add(new MyAgent(c, p,agent.type));
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
        Color c;
        Point2D p;
        World.AgentType type;

        public MyAgent(Color c, Point2D p, World.AgentType type) {
            this.c = c;
            this.p = new Point2D(p.getX()*SQUARESIZE,p.getY()*SQUARESIZE);

            this.type = type;
        }

        public Point2D getPoint() {
            return p;
        }

        public void setPoint(Point2D point) {
            this.p = point;
        }

        public void paint(Graphics g) {

            Graphics2D g2d = (Graphics2D)g;

            AffineTransform old = g2d.getTransform();
            g2d.transform(at);
            g2d.setColor(this.c);
            //g.fillRect((int)p.getX()*SQUARESIZE,(int)p.getY()*SQUARESIZE,SQUARESIZE,SQUARESIZE);
            //g2d.fillOval((int) p.getX() * SQUARESIZE, (int) p.getY() * SQUARESIZE, SQUARESIZE, SQUARESIZE);

            g2d.fillOval((int) p.getX(), (int) p.getY(), SQUARESIZE, SQUARESIZE);
            try {
                BufferedImage image;
                if (!images.stream().anyMatch(p -> p.getKey().equals(type))) {
                    image = ImageIO.read(new File(type.getValue()));
                    images.add(new Pair<>(type,image));
                } else {
                    image = images.stream().filter( p -> p.getKey().equals(type)).findFirst().get().getValue();
                }
                g2d.drawImage(image,(int)p.getX(),(int)p.getY(),null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            g2d.setTransform(old);
        }

    }

} 

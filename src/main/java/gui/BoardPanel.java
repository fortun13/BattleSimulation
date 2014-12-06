package main.java.gui;

//import javafx.geometry.Point2D;

import javafx.geometry.Point2D;
import javafx.util.Pair;
import main.java.agents.World;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class BoardPanel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 400;
	public final int SQUARESIZE = 20;

    public Cursor cursor;

    double factor = 0.05;

    private AffineTransform at = new AffineTransform();

    private ArrayList<Pair<World.AgentType,BufferedImage>> images = new ArrayList<>();

    public int x1, y1, x2, y2;

    public MyAgent clickedAgent = null;
    
    //private MyAgent[][] squares;
	public JPanel innerBoard;

    private ArrayList<MyAgent> agentsList = new ArrayList<>();

    public BoardPanel() {
        super();
        setBackground(Color.WHITE);

        setPreferredSize(new Dimension(WIDTH,HEIGHT));

        innerBoard = new A();

        innerBoard.addMouseWheelListener(new MouseWheelListener() {

            double factor = 0.05;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                int mask = InputEvent.CTRL_DOWN_MASK;

                if ((e.getModifiersEx() & mask) == mask) {
                    System.out.println("event");
                    if (e.getWheelRotation() < 0) {
                        //wheel spun away from user
                        // board should be bigger
                        //at.scale(at.getScaleX() + factor, at.getScaleY() + factor);
                        zoomIn();
                    } else {
                        //wheel spun to user
                        //board should be smaller
                        //at.scale(at.getScaleX() - factor, at.getScaleY() - factor);
                        zoomOut();
                    }
                    innerBoard.revalidate();
                    innerBoard.repaint();
                }
            }
        });

        at.scale(1,1);
    }

    public void generateBoard(int height, int width) {
    	//setPreferredSize(new Dimension(WIDTH,HEIGHT));

    	innerBoard.removeAll();

    	setPreferredSize(new Dimension(width*(SQUARESIZE)+10, height*(SQUARESIZE)+10));

        innerBoard.setPreferredSize(new Dimension(width*(SQUARESIZE)+1, height*(SQUARESIZE)+1));
        add(innerBoard);

        //Color c = new Color(218, 218, 218, 254);
        //Point2D p;
        /*squares = new MySquare[height][width];
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                p = new Point2D(row, col);
                squares[(int)p.getX()][(int)p.getY()] = new MySquare(c,p);
            }
        }*/
        innerBoard.revalidate();
        innerBoard.repaint();
    }
    
    public void drawAgents(java.util.List<World.AgentInTree> agents) {
        innerBoard.removeAll();
        agentsList.clear();
        for (World.AgentInTree agent : agents) {
            Color c;
            Point2D p = agent.pos();

                if (((World.AgentInTree)agent).side == World.AgentsSides.Blues) {
                    if (((World.AgentInTree)agent).isDead)
                        c = new Color(0, 4, 78);
                    else
                        c = new Color(4, 3, 228);
                }
                else {
                    if (((World.AgentInTree)agent).isDead) {
                        c = new Color(75, 0, 0);
                    }
                    else
                        c = new Color(221, 3, 0);
                }

            //System.out.println(p);
            //innerBoard.add(new MySquare(c,p));
            //squares[(int)p.getX()][(int)p.getY()] = new MySquare(c,p);
            agentsList.add(new MyAgent(c, p,agent.type));
        }

        innerBoard.revalidate();
        innerBoard.repaint();
    }

    public ArrayList<MyAgent> getMyAgents() {
        return agentsList;
    }

    public class A extends JPanel {
        @Override
        public void paint(Graphics g) {

            super.paint(g);

            for(MyAgent s : agentsList) {
                s.paint(g);
            }

            if (cursor != null)
                setCursor(cursor);

            /*for (int row = 0; row < squares.length; row++)
                for (int col = 0; col < squares[row].length; col++)
                    squares[row][col].paint(g);

            int height = squares.length;
            for (int row = 0; row < squares.length; row++) {
                for (int col = 0; col < squares[row].length+1; col++) {
//            	innerBoard.add(squares[row][col]);
//                squares[row][col].paint(g);
                    g.drawLine(col * SQUARESIZE, 0, col * SQUARESIZE, height*SQUARESIZE);
                    g.drawLine(0, row * SQUARESIZE, squares[row].length*SQUARESIZE, row * SQUARESIZE);
                }
                g.drawLine(squares.length*SQUARESIZE,0,squares.length*SQUARESIZE,height*SQUARESIZE);
            }
            g.drawLine(0,height*SQUARESIZE,squares[0].length*SQUARESIZE,height*SQUARESIZE);*/


        }
    }

    public void zoomIn() {
        Dimension n = innerBoard.getPreferredSize();
        n.setSize(((n.width+1)+(n.width)*factor),((n.height+1)+(n.height)*factor));
        innerBoard.setPreferredSize(n);
        AffineTransform at2 = new AffineTransform();
        at2.scale(at.getScaleX() + factor, at.getScaleY() + factor);
        at = at2;
        //at.scale(at.getScaleX() + factor, at.getScaleY() + factor);
        System.out.println("Scale: " + at.getScaleX() + " " + at.getScaleY());
        innerBoard.revalidate();
        innerBoard.repaint();
    }

    public void zoomOut() {
        Dimension n = innerBoard.getPreferredSize();
        n.setSize(((n.width+1)-(n.width)*factor),((n.height+1)-(n.height)*factor));
        innerBoard.setPreferredSize(n);
        AffineTransform at2 = new AffineTransform();
        at2.scale(at.getScaleX() - factor, at.getScaleY() - factor);
        at = at2;
        //at.scale(at.getScaleX() - factor, at.getScaleY() - factor);
        System.out.println("Scale: " + at.getScaleX() + " " + at.getScaleY());
        innerBoard.revalidate();
        innerBoard.repaint();
    }

    public class MyAgent extends JComponent {

        Color c;
        Point2D p;
        World.AgentType type;

        public MyAgent(Color c, Point2D p, World.AgentType type) {
            this.c = c;
            //this.p = p;

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
            //g2d.setTransform(at);

            g2d.transform(at);

            g2d.setColor(this.c);
            //g.fillRect((int)p.getX()*SQUARESIZE,(int)p.getY()*SQUARESIZE,SQUARESIZE,SQUARESIZE);
            //g2d.fillOval((int) p.getX() * SQUARESIZE, (int) p.getY() * SQUARESIZE, SQUARESIZE, SQUARESIZE);

            g2d.fillOval((int) p.getX(), (int) p.getY(), SQUARESIZE, SQUARESIZE);

            //TODO I think images should be stored somewhere, and here we should use them to draw, but for now it works
            try {
                BufferedImage image;
                if (!images.stream().anyMatch(p -> p.getKey().equals(type))) {
                    image = ImageIO.read(new File(type.getValue()));
                    images.add(new Pair<>(type,image));
                } else {
                    image = ((Pair< World.AgentType,BufferedImage>)images.stream().filter( p -> p.getKey().equals(type)).toArray()[0]).getValue();
                }
                //BufferedImage image = ImageIO.read(new File(type.getValue()));

                //g2d.drawImage(image, (int) p.getX() * SQUARESIZE, (int) p.getY() * SQUARESIZE, null);
                g2d.drawImage(image,(int)p.getX(),(int)p.getY(),null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            g2d.setTransform(old);
        }

    }
} 

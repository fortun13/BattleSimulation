package main.java.gui;

import javafx.geometry.Point2D;
import main.java.agents.World;
import main.java.utils.KdTree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class BoardPanel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 400;
	private final int SQUARESIZE = 10;
    
    //private MyAgent[][] squares;
	private JPanel innerBoard;

    private ArrayList<MyAgent> agentsList = new ArrayList<>();

    public BoardPanel() {
        super();
        setBackground(Color.WHITE);

        setPreferredSize(new Dimension(WIDTH,HEIGHT));

    }

    public void generateBoard(int height, int width) {
    	//setPreferredSize(new Dimension(WIDTH,HEIGHT));

    	this.removeAll();

    	setPreferredSize(new Dimension(width*(SQUARESIZE)+10, height*(SQUARESIZE)+10));

        innerBoard = new A();
        innerBoard.setPreferredSize(new Dimension(width*(SQUARESIZE)+1, height*(SQUARESIZE)+1));
        add(innerBoard);

        Color c = new Color(218, 218, 218, 254);
        Point2D p;
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
    
    public void drawAgents(java.util.List<? extends KdTree.Placed> agents) {
        innerBoard.removeAll();
        agentsList.clear();
        for (KdTree.Placed agent : agents) {
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
            agentsList.add(new MyAgent(c, p));
        }

        innerBoard.revalidate();
        innerBoard.repaint();
    }

    public class A extends JPanel {
        @Override
        public void paint(Graphics g) {

            super.paint(g);

            for(MyAgent s : agentsList) {
                s.paint(g);
            }

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

    class MyAgent extends JComponent {

        Color c;
        Point2D p;
        public MyAgent(Color c, Point2D p) {
            this.c = c;
            this.p = p;
        }

        public void paint(Graphics g) {
            g.setColor(this.c);
            //g.fillRect((int)p.getX()*SQUARESIZE,(int)p.getY()*SQUARESIZE,SQUARESIZE,SQUARESIZE);
            g.fillOval((int)p.getX()*SQUARESIZE,(int)p.getY()*SQUARESIZE,SQUARESIZE,SQUARESIZE);
        }

    }
} 

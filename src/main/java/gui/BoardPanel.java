package main.java.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class BoardPanel extends JPanel {
	
	
	private final int SQUARESIZE = 20;
    
    private Square[][] squares;
    
	private JPanel innerBoard;

    public class A extends JPanel {
        @Override
        public void paint(Graphics g) {

            super.paint(g);

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
            g.drawLine(0,height*SQUARESIZE,squares[0].length*SQUARESIZE,height*SQUARESIZE);
        }
    }

    //public final int WIDTH = COLS*(SQUARESIZE+7);
    //public final int HEIGHT = ROWS*SQUARESIZE;
   
    //public final int HEIGHT = ROWS*(SQUARESIZE+7);
	
	private final int WIDTH = 800;
	private final int HEIGHT = 400;
    
    public BoardPanel() {
        super();
        setBackground(Color.WHITE);
                
        setPreferredSize(new Dimension(WIDTH,HEIGHT));

        //innerBoard = new JPanel(new GridLayout(ROWS, COLS)) {

            /**
             * Override the preferred size to return the largest it can, in
             * a square shape.  Must (must, must) be added to a GridBagLayout
             * as the only component (it uses the parent as a guide to size)
             * with no GridBagConstaint (so it is centered).
             */
            /*@Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize = null;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int)d.getWidth(),(int)d.getHeight());
                } else if (c!=null &&
                        c.getWidth()>d.getWidth() &&
                        c.getHeight()>d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (w>h ? h : w);
                return new Dimension(s,s);
            }*/

        //};


        
    }
    
    public void generateBoard(int height, int width) {
    	//setPreferredSize(new Dimension(WIDTH,HEIGHT));
    	
    	this.removeAll();
    	
    	setPreferredSize(new Dimension(width*(SQUARESIZE)+10, height*(SQUARESIZE)+10));

        innerBoard = new A();

        innerBoard.setPreferredSize(new Dimension(width*(SQUARESIZE)+1, height*(SQUARESIZE)+1));

//        innerBoard.setBackground(Color.black);
        add(innerBoard);
        
        squares = new Square[height][width];

        // create the chess board squares
//        Insets buttonMargin = new Insets(0, 0, 0, 0);
//        for (int row = 0; row < squares.length; row++) {
//            for (int col = 0; col < squares[row].length; col++) {
////                JButton b = new JButton();
////                b.setMargin(buttonMargin);
////                ImageIcon icon = new ImageIcon(new BufferedImage(SQUARESIZE, SQUARESIZE, BufferedImage.TYPE_INT_ARGB));
////                b.setIcon(icon);
////                /*if ((jj % 2 == 1 && ii % 2 == 1) || (jj % 2 == 0 && ii % 2 == 0)) {
////                    b.setBackground(Color.WHITE);
////                } else {
////                    b.setBackground(Color.BLACK);
////                }*/
////                b.setBackground(Color.WHITE);
//                squares[row][col] = new Square(new Point(col*SQUARESIZE, row*SQUARESIZE), SQUARESIZE);
//            }
//            System.out.println(row);
//        }
//
//        // fill the black non-pawn piece row
//        Graphics2D g = (Graphics2D) innerBoard.getGraphics();
//        for (int row = 0; row < squares.length; row++) {
//            for (int col = 0; col < squares[row].length; col++) {
////            	innerBoard.add(squares[row][col]);
////                squares[row][col].paint(g);
////                g.drawLine(row * SQUARESIZE, row * SQUARESIZE, 0, 500);
////                g.drawLine(0, 500, col * SQUARESIZE, col * SQUARESIZE);
//            }
//            System.out.println(row);
//        }

        this.revalidate();
    }
} 

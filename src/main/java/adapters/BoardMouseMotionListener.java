package main.java.adapters;

import javafx.geometry.Point2D;
import main.java.gui.BoardPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Created by Fortun on 2014-12-06.
 */
public class BoardMouseMotionListener extends MouseMotionAdapter {

    private BoardPanel board;

    public BoardMouseMotionListener(BoardPanel b) {
        this.board = b;
    }

    public void mouseDragged(MouseEvent e) {

        Point2D p = new Point2D(e.getX(),e.getY());

        if (board.clickedAgent == null) {

            if ((board.getMyAgents().stream().anyMatch(a -> a.getPoint().distance(p) < board.SQUARESIZE))) {
                //System.out.println("Found");
                board.clickedAgent = (BoardPanel.MyAgent) board.getMyAgents()
                        .stream()
                        .filter(l -> l.getPoint().distance(p) < board.SQUARESIZE)
                        .toArray()[0];
            }
        } else {
            board.x2 = e.getX();
            board.y2 = e.getY();
            int x = (int) board.clickedAgent.getPoint().getX() + board.x2 - board.x1;
            int y = (int) board.clickedAgent.getPoint().getY() + board.y2 - board.y1;
            board.x1 = board.x2;
            board.y1 = board.y2;
            board.clickedAgent.setPoint(new Point2D(x, y));
        }

        board.innerBoard.repaint();
    }

    public void mouseMoved(MouseEvent e) {
        //System.out.println("Mouse moved");

        Point2D p = new Point2D(e.getX(),e.getY());
        if (!board.getMyAgents().isEmpty()) {
            //System.out.println("Not empty");
            if ((board.getMyAgents().stream().anyMatch( a -> a.getPoint().distance(p)<board.SQUARESIZE))) {
                //System.out.println("Found");
                board.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            } else {
                board.cursor = Cursor.getDefaultCursor();
            }
            board.innerBoard.repaint();
        }
    }

}
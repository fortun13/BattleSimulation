package main.java.adapters;

import javafx.geometry.Point2D;
import main.java.gui.BoardPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Created by Fortun on 2014-12-06.
 *
 */
public class BoardMouseMotionListener extends MouseMotionAdapter {

    private BoardPanel board;
    public boolean simulationStarted = false;

    public BoardMouseMotionListener(BoardPanel b) {
        this.board = b;
    }

    public void mouseDragged(MouseEvent e) {

        if (simulationStarted)
            return;

        Point2D tmp = new Point2D(0,0);
        Point2D p = new Point2D(e.getX(),e.getY());

        if (board.selectedAgent == null) {

            if (cursorOnAgent(tmp, p)) {
                //System.out.println("Found");
                board.selectedAgent = (BoardPanel.MyAgent) board.getMyAgents()
                        .stream()
                        .filter(l -> (tmp.add(
                                l.getPoint().getX()+l.getAgent().type.getSize()/2,
                                l.getPoint().getY()+l.getAgent().type.getSize()/2))
                                .distance(p) < l.getAgent().type.getSize()/2)
                        .toArray()[0];
            }
        } else {
            board.x2 = e.getX();
            board.y2 = e.getY();
            int x = (int) board.selectedAgent.getPoint().getX() + board.x2 - board.x1;
            int y = (int) board.selectedAgent.getPoint().getY() + board.y2 - board.y1;
            board.x1 = board.x2;
            board.y1 = board.y2;
            board.selectedAgent.setPoint(new Point2D(x, y));
        }

        board.innerBoard.repaint();
    }

    public void mouseMoved(MouseEvent e) {
        Point2D p = new Point2D(e.getX(),e.getY());
        Point2D tmp = new Point2D(0,0);
        if (!board.getMyAgents().isEmpty()) {
            //System.out.println("Not empty");
            if (cursorOnAgent(tmp, p)) {
                //System.out.println("Found");
                board.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            } else {
                board.cursor = Cursor.getDefaultCursor();
            }
            board.innerBoard.repaint();
        }
    }

    private boolean cursorOnAgent(Point2D tmp, Point2D p) {
        return board.getMyAgents().stream().anyMatch( a -> (tmp.add(
                a.getPoint().getX()+a.getAgent().type.getSize()/2,
                a.getPoint().getY()+a.getAgent().type.getSize()/2))
                .distance(p) < (a.getAgent().type.getSize()/2));
    }

}

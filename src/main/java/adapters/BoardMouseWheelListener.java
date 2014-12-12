package main.java.adapters;

import main.java.gui.BoardPanel;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

/**
 * Created by Fortun on 2014-12-12.
 */
public class BoardMouseWheelListener implements MouseWheelListener {

    public boolean simulationStarted = true;

    private double factor = 0.05;

    private BoardPanel board;

    public BoardMouseWheelListener(BoardPanel b) {
        board = b;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //System.out.println("event");
        if (!simulationStarted)
            return;

        int mask = InputEvent.CTRL_DOWN_MASK;

        if ((e.getModifiersEx() & mask) == mask) {

            Dimension n = board.innerBoard.getPreferredSize();
            AffineTransform at2 = new AffineTransform();
            if (e.getWheelRotation() < 0) {
                n.setSize(((n.width) + (n.width) * factor), ((n.height) + (n.height) * factor));
                at2.scale(board.at.getScaleX() + factor, board.at.getScaleY() + factor);
            } else {
                n.setSize(((n.width)-(n.width)*factor),((n.height)-(n.height)*factor));
                at2.scale(board.at.getScaleX() - factor, board.at.getScaleY() - factor);
            }
            board.innerBoard.setPreferredSize(n);
            Dimension tmp = new Dimension(n.width+10,n.height+10);
            board.setPreferredSize(tmp);
            board.at = at2;
            board.innerBoard.revalidate();
            board.innerBoard.repaint();
        }
    }
}


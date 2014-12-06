package main.java.adapters;

import main.java.gui.BoardPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Fortun on 2014-12-06.
 */
public class BoardMouseListener extends MouseAdapter {

    private BoardPanel board;

    public BoardMouseListener(BoardPanel b) {
        board = b;
    }

    public void mousePressed(MouseEvent e) {

        board.x1 = e.getX();
        board.y1 = e.getY();
    }
    public void mouseReleased(MouseEvent e) {
        board.clickedAgent = null;
    }

    public void mouseClicked(MouseEvent e) {

    }
}

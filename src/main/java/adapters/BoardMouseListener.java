package main.java.adapters;

import javafx.geometry.Point2D;
import main.java.gui.BoardPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        if (board.clickedAgent != null)
            while(!ifCollisionMove(board.clickedAgent));

        /*if (board.getMyAgents().stream().anyMatch(a -> (a.getPoint().distance(board.clickedAgent.getPoint()) < board.SQUARESIZE) && (!a.equals(board.clickedAgent)))) {
            List<BoardPanel.MyAgent> lst = board.getMyAgents()
                    .stream()
                    .filter(l -> l.getPoint().distance(board.clickedAgent.getPoint()) < board.SQUARESIZE && !l.equals(board.clickedAgent))
                    .collect(Collectors.toList());

            //System.out.println(lst.get(0).getPoint());

            //System.out.println("Collision!");
        }
        board.clickedAgent = null;*/
    }

    private boolean ifCollisionMove(BoardPanel.MyAgent position) {
        if (board.getMyAgents()
                .stream()
                .anyMatch(a -> (a.getPoint().distance(position.getPoint()) < board.SQUARESIZE) && (!a.equals(position)))) {
            List<BoardPanel.MyAgent> lst = board.getMyAgents()
                    .stream()
                    .filter(l -> l.getPoint().distance(board.clickedAgent.getPoint()) < board.SQUARESIZE && !l.equals(board.clickedAgent))
                    .collect(Collectors.toList());
            ArrayList<Integer[]> vectors = new ArrayList<>();
            Point2D oldPos = position.getPoint();
            Random rnd = new Random();
            for (BoardPanel.MyAgent agent : lst) {
                int x = (int) (oldPos.getX() - agent.getPoint().getX());
                int y = (int) (oldPos.getY() - agent.getPoint().getY());
                //System.out.println("x: " + x + " y: " + y);
                Integer[] vec = new Integer[2];
                if (x<0)
                    vec[0] = -(board.SQUARESIZE + x);
                else
                    vec[0] = board.SQUARESIZE - x;
                if (y<0)
                    vec[1] = -(board.SQUARESIZE + y);
                else
                    vec[1] = board.SQUARESIZE - y;

                vec[0] = (rnd.nextInt(3)-1);
                vec[1] = (rnd.nextInt(3)-1);
                vectors.add(vec);
            }
            //System.out.println(vectors.get(0)[0] + " " + vectors.get(0)[1]);
            Integer[] newVector = vectors
                    .parallelStream()
                    .reduce(new Integer[]{0, 0}, (acc, x) -> new Integer[]{acc[0] + x[0], acc[1] + x[1]});
            //System.out.println("Reduce X: " + newVector[0] + " Y: " + newVector[1]);
            position.setPoint(new Point2D(oldPos.getX() + newVector[0], oldPos.getY() + newVector[1]));

            //board.innerBoard.repaint();

            return false;

        } else {
            board.clickedAgent = null;
            board.innerBoard.repaint();
            return true;
            //no collision
            //change position of agent in tree? (probably save in buffer until user will click start simulation button)
        }
    }

    public void mouseClicked(MouseEvent e) {

    }
}

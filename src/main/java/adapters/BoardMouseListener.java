package main.java.adapters;

import javafx.geometry.Point2D;
import main.java.gui.BoardPanel;
import main.java.gui.MainFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Fortun on 2014-12-06.
 *
 */
public class BoardMouseListener extends MouseAdapter {

    private BoardPanel board;
    public boolean simulationStarted;

    public BoardMouseListener(BoardPanel b) {
        board = b;
    }

    public void mousePressed(MouseEvent e) {

        board.x1 = e.getX();
        board.y1 = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        if (simulationStarted)
            return;
        ifCollisionMove(board.selectedAgent);
    }

    private void ifCollisionMove(BoardPanel.MyAgent position) {
        while (position != null) {
            if (board.getMyAgents()
                    .stream()
                    .anyMatch(a -> (a.getPoint().distance(position.getPoint()) < position.getAgent().type.getSize()) && (!a.equals(position)))) {
                int size = position.getAgent().type.getSize();
                List<BoardPanel.MyAgent> lst = board.getMyAgents()
                        .stream()
                        .filter(l -> l.getPoint().distance(board.selectedAgent.getPoint()) < size && !l.equals(board.selectedAgent))
                        .collect(Collectors.toList());

                ArrayList<Integer[]> vectors = new ArrayList<>();
                Point2D oldPos = position.getPoint();
                Random rnd = new Random();
                for (BoardPanel.MyAgent agent : lst) {
                    int x = (int) (oldPos.getX() - agent.getPoint().getX());
                    int y = (int) (oldPos.getY() - agent.getPoint().getY());
                    //System.out.println("x: " + x + " y: " + y);
                    Integer[] vec = new Integer[2];
                    if (x < 0)
                        vec[0] = -(size + x);
                    else
                        vec[0] = size - x;
                    if (y < 0)
                        vec[1] = -(size + y);
                    else
                        vec[1] = size - y;

                    vec[0] = (rnd.nextInt(3) - 1);
                    vec[1] = (rnd.nextInt(3) - 1);
                    vectors.add(vec);
                }
                //System.out.println(vectors.get(0)[0] + " " + vectors.get(0)[1]);
                Integer[] newVector = vectors
                        .parallelStream()
                        .reduce(new Integer[]{0, 0}, (acc, x) -> new Integer[]{acc[0] + x[0], acc[1] + x[1]});
                //System.out.println("Reduce X: " + newVector[0] + " Y: " + newVector[1]);
                position.setPoint(new Point2D(oldPos.getX() + newVector[0], oldPos.getY() + newVector[1]));

                //board.innerBoard.repaint();

            } else {
                board.selectedAgent = null;
                board.innerBoard.repaint();
                break;
                //no collision
                //change position of agent in tree? (probably save in buffer until user will click start simulation button)
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        Point2D p = new Point2D(e.getX(),e.getY());
        Point2D tmp = new Point2D(0,0);
        if (cursorOnAgent(tmp,p)) {
            BoardPanel.MyAgent agent = (BoardPanel.MyAgent) board.getMyAgents()
                    .stream()
                    .filter(l -> (tmp.add(
                            l.getPoint().getX()+l.getAgent().type.getSize()/2,
                            l.getPoint().getY()+l.getAgent().type.getSize()/2))
                            .distance(p) < l.getAgent().type.getSize()/2)
                    .toArray()[0];
            ((MainFrame)board.getTopLevelAncestor()).updateStatistics(agent);
        } else {
            ((MainFrame)board.getTopLevelAncestor()).cleanStatistics();

        }
    }

    private boolean cursorOnAgent(Point2D tmp, Point2D p) {
        return board.getMyAgents().stream().anyMatch( a -> (tmp.add(
                a.getPoint().getX()+a.getAgent().type.getSize()/2,
                a.getPoint().getY()+a.getAgent().type.getSize()/2))
                .distance(p) < (a.getAgent().type.getSize()/2));
    }
}

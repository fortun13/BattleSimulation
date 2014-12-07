package main.java.adapters;

import javafx.util.Pair;
import main.java.gui.MainFrame;
import main.java.gui.SideOptionPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class Controller {
	
	private final MainFrame frame;
    //private final ServerAgent server;

    private BoardMouseMotionListener motionListener;
    private BoardMouseListener mouseListener;
	
	public Controller(MainFrame f) {
		frame = f;

        motionListener = new BoardMouseMotionListener(frame.getBoardPanel());
        mouseListener = new BoardMouseListener(frame.getBoardPanel());

        frame.getOptionsPanel().generateButtonAddActionListener((e) -> {
            Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
            //System.out.println(size);
            frame.getBoardPanel().generateBoard(size.getKey(), size.getValue());
            frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
            frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
        });
        
        frame.getOptionsPanel().startSimulationButtonAddActionListener(e -> {
            setMouseWheelListenerForBoard();
            frame.getBoardPanel().innerBoard.removeMouseMotionListener(motionListener);
            frame.getBoardPanel().innerBoard.removeMouseListener(mouseListener);

            frame.server.startSimulation();
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.server.doDelete();
                super.windowClosing(e);
            }
        });

        frame.getOptionsPanel().setSidePanelsSliderListener(e -> {
            JSlider source = (JSlider) e.getSource();
            SideOptionPanel p = findParent(source, SideOptionPanel.class);
            //TODO it's very unefficient
            // ... although - even if I define it inside SideOptionPanel (so no finding parent and so on), it's still eating CPU...
            p.sliderMoved();
        });

        frame.getOptionsPanel().spawnAgentsAddActionListener((e) -> frame.server.prepareSimulation(frame.getOptionsPanel().getBluesAgentsNumber(),frame.getOptionsPanel().getRedsAgentsNumber()));
    }

    public static <T extends Container> T findParent(Component comp, Class<T> clazz)  {
        if (comp == null)
            return null;
        if (clazz.isInstance(comp))
            return (clazz.cast(comp));
        else
            return findParent(comp.getParent(), clazz);
    }

    private void setMouseWheelListenerForBoard() {
        frame.getBoardPanel().innerBoard.addMouseWheelListener(new MouseWheelListener() {
            private double factor = 0.05;
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int mask = InputEvent.CTRL_DOWN_MASK;

                if ((e.getModifiersEx() & mask) == mask) {
                    //System.out.println("event");
                    Dimension n = frame.getBoardPanel().innerBoard.getPreferredSize();
                    AffineTransform at2 = new AffineTransform();
                    if (e.getWheelRotation() < 0) {
                        n.setSize(((n.width + 1) + (n.width) * factor), ((n.height + 1) + (n.height) * factor));
                        at2.scale(frame.getBoardPanel().at.getScaleX() + factor, frame.getBoardPanel().at.getScaleY() + factor);
                    } else {
                        n.setSize(((n.width+1)-(n.width)*factor),((n.height+1)-(n.height)*factor));
                        at2.scale(frame.getBoardPanel().at.getScaleX() - factor, frame.getBoardPanel().at.getScaleY() - factor);
                    }
                    //System.out.println(n);
                    frame.getBoardPanel().innerBoard.setPreferredSize(n);
                    n.height = n.height+10;
                    n.width = n.width+10;
                    frame.getBoardPanel().setPreferredSize(n);
                    frame.getBoardPanel().at = at2;
                    frame.getBoardPanel().innerBoard.revalidate();
                    frame.getBoardPanel().innerBoard.repaint();
                }
            }
        });
    }

}

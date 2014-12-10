package main.java.adapters;

import javafx.util.Pair;
import main.java.agents.ServerAgent;
import main.java.gui.MainFrame;
import main.java.gui.SideOptionPanel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Controller {
	
	private final MainFrame frame;
    private final ServerAgent server;

    private BoardMouseMotionListener motionListener;
    private BoardMouseListener mouseListener;
	
	public Controller(MainFrame f, ServerAgent s) {
		frame = f;
        server = s;

        motionListener = new BoardMouseMotionListener(frame.getBoardPanel());
        mouseListener = new BoardMouseListener(frame.getBoardPanel());

        frame.getOptionsPanel().generateButtonAddActionListener((e) -> {
            Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
            //System.out.println(size);
            frame.getBoardPanel().generateBoard(size.getKey(), size.getValue());
            frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
            frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
        });

        frame.startSimulationButtonAddActionListener(e -> {
            setMouseWheelListenerForBoard();
            frame.getBoardPanel().innerBoard.removeMouseMotionListener(motionListener);
            frame.getBoardPanel().innerBoard.removeMouseListener(mouseListener);

            frame.server.startSimulation();
        });
        
        /*frame.getOptionsPanel().startSimulationButtonAddActionListener(e -> {
            setMouseWheelListenerForBoard();
            frame.getBoardPanel().innerBoard.removeMouseMotionListener(motionListener);
            frame.getBoardPanel().innerBoard.removeMouseListener(mouseListener);

            frame.server.startSimulation();
        });*/

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.server.doDelete();
                super.windowClosing(e);
            }
        });

        frame.getOptionsPanel().openFileAddActionListener(e -> {
            int returnVal = frame.getOptionsPanel().getFileChooser().showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = frame.getOptionsPanel().getFileChooser().getSelectedFile();
                //This is where a real application would open the file.
                String content = "";
                Scanner scanner = null;
                try {
                    scanner = new Scanner(new FileInputStream(file));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                while (scanner.hasNext())
                    content += scanner.nextLine();

                scanner.close();
                JSONObject obj = new JSONObject(content);
                JSONArray agents = obj.getJSONArray("agents");

                HashMap<String,ArrayList<JSONObject>> map = new HashMap<>();

                for (int i=0;i<agents.length();i++) {
                    JSONObject agent = agents.getJSONObject(i);

                    if (map.containsKey(agent.get("type").toString()))
                        map.get(agent.get("type")).add(agent);
                    else {
                        ArrayList<JSONObject> lst = new ArrayList<>();
                        lst.add(agent);
                        map.put(agent.get("type").toString(),lst);
                    }
                }


                frame.getBoardPanel().generateBoard(obj.getInt("boardHeight"), obj.getInt("boardWidth"));
                frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
                frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
                server.prepareSimulation(map, obj.getInt("boardWidth"));
            } else {
                //file not been choosen do nothing
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
                        n.setSize(((n.width) + (n.width) * factor), ((n.height) + (n.height) * factor));
                        at2.scale(frame.getBoardPanel().at.getScaleX() + factor, frame.getBoardPanel().at.getScaleY() + factor);
                    } else {
                        n.setSize(((n.width)-(n.width)*factor),((n.height)-(n.height)*factor));
                        at2.scale(frame.getBoardPanel().at.getScaleX() - factor, frame.getBoardPanel().at.getScaleY() - factor);
                    }
                    frame.getBoardPanel().innerBoard.setPreferredSize(n);
                    Dimension tmp = new Dimension(n.width+10,n.height+10);
                    frame.getBoardPanel().setPreferredSize(tmp);
                    frame.getBoardPanel().at = at2;
                    frame.getBoardPanel().innerBoard.revalidate();
                    frame.getBoardPanel().innerBoard.repaint();
                }
            }
        });
    }

}

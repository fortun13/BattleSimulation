package main.java.adapters;

import javafx.util.Pair;
import main.java.agents.ServerAgent;
import main.java.gui.MainFrame;
import main.java.gui.SideOptionPanel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Controller {
	
	private final MainFrame frame;
    private final ServerAgent server;

    private BoardMouseMotionListener motionListener = null;
    private BoardMouseListener mouseListener = null;

    private BoardMouseWheelListener mouseWheelListener = null;
	
	public Controller(MainFrame f, ServerAgent s) {
		frame = f;
        server = s;

        frame.getOptionsPanel().generateButtonAddActionListener((e) -> {

            Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
            //System.out.println(size);
            frame.getBoardPanel().generateBoard(size.getKey(), size.getValue());

        });

        frame.startSimulationButtonAddActionListener(e -> {
            if (mouseWheelListener == null) {
                mouseWheelListener = new BoardMouseWheelListener(frame.getBoardPanel());
                frame.getBoardPanel().addMouseWheelListener(mouseWheelListener);
            } else {
                mouseWheelListener.simulationStarted = true;
            }

            motionListener.simulationStarted = true;
            mouseListener.simulationStarted = true;
            frame.server.startSimulation();
        });

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
                    while (scanner.hasNext())
                        content += scanner.nextLine();

                    scanner.close();
                    JSONObject obj = new JSONObject(content);
                    JSONArray agents = obj.getJSONArray("agents");

                    HashMap<String, ArrayList<JSONObject>> map = new HashMap<>();

                    for (int i = 0; i < agents.length(); i++) {
                        JSONObject agent = agents.getJSONObject(i);

                        if (map.containsKey(agent.get("type").toString()))
                            map.get(agent.get("type").toString()).add(agent);
                        else {
                            ArrayList<JSONObject> lst = new ArrayList<>();
                            lst.add(agent);
                            map.put(agent.get("type").toString(), lst);
                        }
                    }
                    frame.getBoardPanel().generateBoard(obj.getInt("boardHeight"), obj.getInt("boardWidth"));
                    frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
                    frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
                    server.prepareSimulation(map, obj.getInt("boardWidth"));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        frame.getOptionsPanel().setSidePanelsSliderListener(e -> {
            JSlider source = (JSlider) e.getSource();
            SideOptionPanel p = findParent(source, SideOptionPanel.class);
            //TODO it's very unefficient
            // ... although - even if I define it inside SideOptionPanel (so no finding parent and so on), it's still eating CPU...
            p.sliderMoved();
        });

        frame.spawnAgentsAddActionListener((e) -> {
            if (motionListener == null) {
                motionListener = new BoardMouseMotionListener(frame.getBoardPanel());
                mouseListener = new BoardMouseListener(frame.getBoardPanel());
                frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
                frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
            } else {
                motionListener.simulationStarted = false;
                mouseListener.simulationStarted = false;
                mouseWheelListener.simulationStarted = false;
                Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
                //System.out.println(size);
                frame.getBoardPanel().generateBoard(size.getKey(), size.getValue());
                //frame.getBoardPanel().resetScale();
            }
            frame.server.prepareSimulation(frame.getOptionsPanel().getBluesAgents(),frame.getOptionsPanel().getRedsAgents());
        });

    }

    public static <T extends Container> T findParent(Component comp, Class<T> clazz)  {
        if (comp == null)
            return null;
        if (clazz.isInstance(comp))
            return (clazz.cast(comp));
        else
            return findParent(comp.getParent(), clazz);
    }

}

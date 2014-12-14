package main.java.adapters;

import edu.wlu.cs.levy.CG.KeySizeException;
import javafx.util.Pair;
import main.java.gui.MainFrame;
import main.java.gui.SideOptionPanel;
import main.java.utils.AgentInTree;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Controller {
	
	private final MainFrame frame;

    private BoardMouseMotionListener motionListener = null;
    private BoardMouseListener mouseListener = null;

    private BoardMouseWheelListener mouseWheelListener = null;
	
	public Controller(MainFrame f) {
		frame = f;

        frame.startSimulationButtonAddActionListener(e -> {
            if (mouseWheelListener == null) {
                mouseWheelListener = new BoardMouseWheelListener(frame.getBoardPanel());
                frame.getBoardPanel().addMouseWheelListener(mouseWheelListener);
            } else {
                mouseWheelListener.simulationStarted = true;
            }

            motionListener.simulationStarted = true;
            mouseListener.simulationStarted = true;
            frame.getBoardPanel().simulationStarted = true;
            frame.server.startSimulation();
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.server.doDelete();
                super.windowClosing(e);
            }
        });

        createOpenFileActionListener();

        createSaveToFileActionListener();

        frame.getOptionsPanel().setSidePanelsSliderListener(e -> {
            JSlider source = (JSlider) e.getSource();
            SideOptionPanel p = findParent(source, SideOptionPanel.class);
            p.sliderMoved();
        });

        frame.spawnAgentsAddActionListener((e) -> {
            Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
            prepareMouseListenersAndBoard(size.getValue(),size.getKey());
            frame.server.prepareSimulation(frame.getOptionsPanel().getBluesAgents(),frame.getOptionsPanel().getRedsAgents(),null,-1,frame.getOptionsPanel().getTimeStep());
        });

    }

    private void prepareMouseListenersAndBoard(int boardWidth, int boardHeight) {
        if (mouseWheelListener != null) {
            mouseWheelListener.simulationStarted = false;
        }
        if (motionListener == null) {
            motionListener = new BoardMouseMotionListener(frame.getBoardPanel());
            mouseListener = new BoardMouseListener(frame.getBoardPanel());
            frame.getBoardPanel().innerBoard.addMouseMotionListener(motionListener);
            frame.getBoardPanel().innerBoard.addMouseListener(mouseListener);
        } else {
            motionListener.simulationStarted = false;
            mouseListener.simulationStarted = false;
        }
        frame.getBoardPanel().simulationStarted = false;
        frame.getBoardPanel().generateBoard(boardHeight, boardWidth);
        frame.getBoardPanel().resetScale();
    }

    private void createOpenFileActionListener() {
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
                    prepareMouseListenersAndBoard(obj.getInt("boardWidth"),obj.getInt("boardHeight"));
                    frame.server.prepareSimulation(null,null,map, obj.getInt("boardWidth"),frame.getOptionsPanel().getTimeStep());
                    frame.getOptionsPanel().setBoardSize(obj.getInt("boardWidth"),obj.getInt("boardHeight"));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void createSaveToFileActionListener() {
        frame.getOptionsPanel().saveToFileAddActionListener(e -> {
            int returnVal = frame.getOptionsPanel().getFileChooser().showSaveDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = frame.getOptionsPanel().getFileChooser().getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(f);
                    Pair<Integer,Integer> size = frame.getOptionsPanel().getBoardSize();
                    JSONWriter w = new JSONWriter(fw);
                    w.object();
                    w.key("boardWidth").value(size.getValue());
                    w.key("boardHeight").value(size.getKey());

                    frame.server.updateTree();

                    double[] testKey = {0,0};
                    double[] upperKey = {size.getValue()*frame.getBoardPanel().SQUARESIZE,size.getKey()*frame.getBoardPanel().SQUARESIZE};
                    try {
                        java.util.List<AgentInTree> lst = frame.server.getWorld().getAgentsTree().range(testKey, upperKey);
                        w.key("agents");
                        w.array();
                        for (AgentInTree a : lst) {
                            w.object();
                            w.key("x").value(a.p.getX());
                            w.key("y").value(a.p.getY());
                            w.key("type").value(a.type);
                            w.key("side").value(a.side);
                            if (a.behaviourClass != null) {
                                String b = a.behaviourClass.getName();
                                w.key("behaviour").value(b.substring(b.lastIndexOf('.')+1));
                            }
                            else
                                w.key("behaviour").value("");
                            w.endObject();
                        }
                        w.endArray();
                        //w.endObject();
                    } catch (KeySizeException e1) {
                        e1.printStackTrace();
                    }
                    w.endObject();
                    fw.flush();
                    fw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
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

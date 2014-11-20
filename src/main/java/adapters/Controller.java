package main.java.adapters;

import javafx.util.Pair;
import main.java.gui.BoardPanel;
import main.java.gui.MainFrame;
import main.java.gui.OptionsPanel;
import main.java.gui.SideOptionPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Controller {
	
	private final MainFrame frame;
	
	public Controller(MainFrame f) {
		frame = f;

        frame.getOptionsPanel().generateButtonAddActionListener((e) -> {
            // TODO Auto-generated method stub
            Pair<Integer, Integer> size = frame.getOptionsPanel().getBoardSize();
            //System.out.println(size);
            frame.getBoardPanel().generateBoard(size.getKey(), size.getValue());
        });
        
        frame.getOptionsPanel().startSimulationButtonAddActionListener((e) -> {
            frame.server.startSimulation(frame.getOptionsPanel().getBluesAgentsNumber(),frame.getOptionsPanel().getRedsAgentsNumber());
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
            //p.getAgentsNumberSpinner().setValue(source.getValue());
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

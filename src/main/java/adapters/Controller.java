package main.java.adapters;

import javafx.util.Pair;
import main.java.gui.BoardPanel;
import main.java.gui.MainFrame;
import main.java.gui.OptionsPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Controller {
	
	private final MainFrame frame;
	private final BoardPanel board;
	private final OptionsPanel options;
	
	public Controller(MainFrame f, BoardPanel b, OptionsPanel o) {
		frame = f;
		board = b;
		options = o;

        options.generateButtonAddActionListener((e)->{
            // TODO Auto-generated method stub
            Pair<Integer,Integer> size = options.getBoardSize();
            //System.out.println(size);
            board.generateBoard(size.getKey(), size.getValue());
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.server.doDelete();
                super.windowClosing(e);
            }
        });
	}

}

package main.java.adapters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.util.Pair;
import main.java.gui.BoardPanel;
import main.java.gui.MainFrame;
import main.java.gui.OptionsPanel;

public class Controller {
	
	private final MainFrame frame;
	private final BoardPanel board;
	private final OptionsPanel options;
	
	public Controller(MainFrame f, BoardPanel b, OptionsPanel o) {
		frame = f;
		board = b;
		options = o;
		
		options.generateButtonAddActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Pair<Integer,Integer> size = options.getBoardSize();
				//System.out.println(size);
				board.generateBoard(size.getKey(), size.getValue());
				
			}
		});
	}

}

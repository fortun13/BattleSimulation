package main.java.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jakub Fortunka on 08.11.14.
 */
public class BoardPanel extends JPanel {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;

    public BoardPanel() {
        super();
        setBackground(Color.WHITE);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
    }
}

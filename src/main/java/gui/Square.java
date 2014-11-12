package main.java.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marek on 2014-11-12.
 */
public class Square extends JComponent {
    Point leftUp;
    int w;

    Square() { super(); }

    public Square(Point lu, int width) {
        super();
        leftUp = lu;
        w = width;
    }

    public void paint(Graphics g) {
        g.drawRect(leftUp.x, leftUp.y, w, w);
    }

    public void setPos(Point p, int width) {
        leftUp = p; w = width;
    }
}

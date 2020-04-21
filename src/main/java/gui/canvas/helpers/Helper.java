package gui.canvas.helpers;

import gui.canvas.Canvas;
import gui.canvas.CanvasItem;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class Helper {
    public Canvas parent;
    public boolean active = false;

    Point currPoint = new Point();
    Point prevPoint = new Point();
    Point delta = new Point();

    public void process(MouseEvent e, CanvasItem item, Component target) {
        prevPoint = currPoint;
        currPoint = e.getPoint();
        delta = new Point(prevPoint.x - currPoint.x, prevPoint.y - currPoint.y);
    }
}

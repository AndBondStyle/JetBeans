package gui.canvas.helpers;

import gui.canvas.CanvasItem;

import java.awt.*;
import java.awt.event.MouseEvent;

public class SelectHelper extends Helper {
    public void process(MouseEvent e, CanvasItem item, Component target) {
        if (e.isConsumed()) return;
        super.process(e, item, target);
        if (item != null && item.isSelectable() && e.getID() == MouseEvent.MOUSE_PRESSED && e.getButton() == MouseEvent.BUTTON1)
            parent.select(item);
        if (item == null && e.getID() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseEvent.BUTTON1)
            parent.select(null);
    }
}

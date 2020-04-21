package gui.canvas.helpers;

import gui.canvas.CanvasItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PanHelper extends Helper {
    Point prevPos = null;

    @Override
    public void process(MouseEvent e, CanvasItem item, Component target) {
        super.process(e, item, target);
        if (!active) {
            if (item == null) parent.cursor = getCursor();
            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                if ((item == null && e.getButton() == MouseEvent.BUTTON1) || e.getButton() == MouseEvent.BUTTON2) {
                    active = true;
                    prevPos = parent.scroll.getViewport().getViewPosition();
                    e.consume();
                }
            }
        } else {
            parent.cursor = getCursor();
            e.consume();
            if (e.getID() == MouseEvent.MOUSE_RELEASED) active = false;
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                Point pos = parent.scroll.getViewport().getViewPosition();
                JViewport viewport = parent.scroll.getViewport();

                if (!pos.equals(prevPos)) {
                    // Looks like mouse wheel was used...
                    Point externalDelta = new Point(pos.x - prevPos.x, pos.y - prevPos.y);
                    prevPoint.translate(externalDelta.x, externalDelta.y);
                    delta = new Point(prevPoint.x - currPoint.x, prevPoint.y - currPoint.y);
                }

                pos.translate(delta.x, delta.y);
                pos.x = Math.min(Math.max(pos.x, 0), parent.content.getWidth() - viewport.getWidth());
                pos.y = Math.min(Math.max(pos.y, 0), parent.content.getHeight() - viewport.getHeight());
                parent.scroll.getViewport().setViewPosition(pos);
                currPoint.translate(delta.x, delta.y);
                prevPos = pos;
            }
        }
    }

    Cursor getCursor() {
        // TODO: ???
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }
}

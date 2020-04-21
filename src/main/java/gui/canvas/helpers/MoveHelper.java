package gui.canvas.helpers;

import gui.canvas.CanvasItem;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MoveHelper extends Helper {
    Component target;
    Point offset;

    public void process(MouseEvent e, CanvasItem item, Component target) {
        super.process(e, item, target);
        if (!active) {
            if (e.isConsumed() || item == null || !e.isShiftDown()) return;
            parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            if (item.isMovable() && e.getID() == MouseEvent.MOUSE_PRESSED && e.getButton() == MouseEvent.BUTTON1) {
                e.consume();
                this.active = true;
                this.target = (Component) item;
                Point pos = ((Component) item).getLocation();
                this.offset = new Point(pos.x - currPoint.x, pos.y - currPoint.y);
            }
        } else {
            e.consume();
            parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            if (e.getID() == MouseEvent.MOUSE_RELEASED && e.getButton() == MouseEvent.BUTTON1) active = false;
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                currPoint.translate(offset.x, offset.y);
                this.target.setLocation(currPoint);
            }
        }
    }
}

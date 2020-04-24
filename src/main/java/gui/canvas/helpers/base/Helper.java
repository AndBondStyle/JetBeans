package gui.canvas.helpers.base;

import gui.canvas.CanvasItem;
import gui.canvas.Canvas;

import java.awt.event.MouseEvent;
import java.awt.*;

public abstract class Helper {
    public Canvas parent;           // Link to Canvas instance
    public MouseEvent event;        // Current event
    public Component target;        // Current target
    public Point point;             // Current cursor point

    public void setParent(Canvas parent) {
        this.parent = parent;
    }

    public void process(MouseEvent e, CanvasItem item) {
        this.event = e;
        this.target = (Component) item;
        this.point = e.getPoint();
    }

    // TODO: Move to "utils" or something
    public boolean checkEvent(Integer button, Integer action) {
        if (button != null) {
            if (action != MouseEvent.MOUSE_RELEASED && action != MouseEvent.MOUSE_CLICKED) {
                // Here we check only methods that imply some button is actually pressed
                int mask = MouseEvent.getMaskForButton(button);
                if ((this.event.getModifiersEx() & mask) != mask) return false;
            } else {
                // Otherwise, we can't use mask
                if (this.event.getButton() != button) return false;
            }
        }
        return action == null || this.event.getID() == action;
    }
}

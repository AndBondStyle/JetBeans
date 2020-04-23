package gui.canvas.helpers;

import gui.canvas.Canvas;
import gui.canvas.CanvasItem;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class Helper {
    public Canvas parent;           // Link to Canvas instance
    public boolean active = false;  // Active state flag
    public MouseEvent event;        // Current event
    public Component target;        // Current target
    public Point point;             // Current cursor point

    boolean checkPossible() {
        return true;
    }

    boolean checkStart() {
        return false;
    }

    boolean checkProgress() {
        return false;
    }

    boolean checkEnd() {
        return false;
    }

    void processPossible() {
    }

    void processStart() {
        this.active = true;
    }

    void processPorgress() {
    }

    void processEnd() {
        this.active = false;
    }

    boolean checkEvent(Integer button, Integer action) {
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

    public void process(MouseEvent e, CanvasItem item) {
        this.event = e;
        this.target = (Component) item;
        this.point = e.getPoint();

        if (!active) {
            if (checkPossible()) processPossible();
            if (checkStart()) processStart();
        } else {
            if (checkProgress()) processPorgress();
            if (checkEnd()) processEnd();
        }
    }
}

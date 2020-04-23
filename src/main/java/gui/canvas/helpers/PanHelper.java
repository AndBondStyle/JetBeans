package gui.canvas.helpers;

import gui.canvas.CanvasItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PanHelper extends Helper {
    Point oldLocation = null;
    Point oldPoint = null;
    int button = -1;

    @Override
    boolean checkStart() {
        if (this.event.isConsumed()) return false;
        if (checkEvent(MouseEvent.BUTTON2, MouseEvent.MOUSE_PRESSED)) return true;
        return this.target == null && checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_PRESSED);
    }

    @Override
    void processStart() {
        super.processStart();
        this.event.consume();
        this.parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

        this.oldLocation = this.parent.scroll.getViewport().getViewPosition();
        this.button = this.event.getButton();
        System.out.println("PAN ACTION STARTED");
    }

    @Override
    boolean checkProgress() {
        if (this.event.isConsumed()) return false;
        this.parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        return checkEvent(this.button, MouseEvent.MOUSE_DRAGGED);
    }

    @Override
    void processPorgress() {
        super.processPorgress();
        this.event.consume();
        Point pos = this.parent.scroll.getViewport().getViewPosition();
        JViewport viewport = this.parent.scroll.getViewport();

        if (!pos.equals(this.oldLocation)) {
            // Looks like mouse wheel was used...
            Point externalDelta = new Point(pos.x - this.oldLocation.x, pos.y - this.oldLocation.y);
            this.oldPoint.translate(externalDelta.x, externalDelta.y);
        }

        Point delta = new Point(this.oldPoint.x - this.point.x, this.oldPoint.y - this.point.y);
        pos.translate(delta.x, delta.y);
        pos.x = Math.min(Math.max(pos.x, 0), this.parent.content.getWidth() - viewport.getWidth());
        pos.y = Math.min(Math.max(pos.y, 0), this.parent.content.getHeight() - viewport.getHeight());
        this.parent.scroll.getViewport().setViewPosition(pos);
        this.point.translate(delta.x, delta.y);
        this.oldLocation = pos;
    }

    @Override
    boolean checkEnd() {
        return checkEvent(this.button, MouseEvent.MOUSE_RELEASED);
    }

    @Override
    void processEnd() {
        super.processEnd();
        System.out.println("PAN ACTION ENDED");
    }

    @Override
    public void process(MouseEvent e, CanvasItem item) {
        this.oldPoint = this.point;
        super.process(e, item);
    }
}

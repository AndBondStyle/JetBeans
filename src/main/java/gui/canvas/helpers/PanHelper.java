package gui.canvas.helpers;

import gui.canvas.CanvasItem;
import gui.canvas.helpers.base.ToggleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PanHelper extends ToggleHelper {
    public Point oldLocation = null;
    public Point oldPoint = null;
    public int button = -1;

    @Override
    public void process(MouseEvent e, CanvasItem item) {
        this.oldPoint = this.point;
        super.process(e, item);
    }

    @Override
    public Cursor getCursor() {
        if (!this.active) return null;
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    @Override
    public boolean isPossible() {
        return !this.event.isConsumed();
    }

    @Override
    public boolean isStarted() {
        boolean middleButton = checkEvent(MouseEvent.BUTTON2, MouseEvent.MOUSE_PRESSED);
        boolean dragBackground = this.target == null && checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_PRESSED);
        if (!middleButton && !dragBackground) return false;
        this.event.consume();
        this.oldLocation = this.parent.scroll.getViewport().getViewPosition();
        this.button = this.event.getButton();
        return true;
    }

    @Override
    public void handleProgress() {
        if (this.event.isConsumed()) return;
        if (!checkEvent(this.button, MouseEvent.MOUSE_DRAGGED)) return;
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
    public boolean isEnded() {
        return checkEvent(this.button, MouseEvent.MOUSE_RELEASED);
    }
}

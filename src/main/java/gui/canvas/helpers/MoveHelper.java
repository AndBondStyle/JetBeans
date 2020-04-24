package gui.canvas.helpers;

import gui.canvas.helpers.base.ToggleHelper;
import gui.canvas.CanvasItem;

import java.awt.event.MouseEvent;
import java.awt.*;

public class MoveHelper extends ToggleHelper {
    public Component moveTarget;
    public Point offset;

    @Override
    public Cursor getCursor() {
        return this.possible || this.active ? Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) : null;
    }

    @Override
    public boolean isPossible() {
        return !this.event.isConsumed()
                && this.event.isShiftDown()
                && this.target != null
                && ((CanvasItem) this.target).isMovable();
    }

    @Override
    public boolean isStarted() {
        if (!checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_PRESSED)) return false;
        this.event.consume();
        this.moveTarget = this.target;
        Point pos = this.target.getLocation();
        this.offset = new Point(pos.x - point.x, pos.y - point.y);
        return true;
    }

    @Override
    public void handleProgress() {
        if (this.event.isConsumed()) return;
        if (!checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_DRAGGED)) return;
        this.point.translate(this.offset.x, this.offset.y);
        this.moveTarget.setLocation(this.point);
    }

    @Override
    public boolean isEnded() {
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_RELEASED);
    }
}

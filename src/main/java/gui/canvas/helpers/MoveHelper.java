package gui.canvas.helpers;

import gui.canvas.CanvasItem;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MoveHelper extends Helper {
    Component moveTarget;
    Point offset;

    @Override
    boolean checkPossible() {
        return !this.event.isConsumed()
                && this.event.isShiftDown()
                && this.target != null
                && ((CanvasItem) this.target).isMovable();
    }

    @Override
    void processPossible() {
        this.parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    @Override
    boolean checkStart() {
        if (!checkPossible()) return false;
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_PRESSED);
    }

    @Override
    void processStart() {
        super.processStart();
        this.event.consume();
        this.parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

        this.moveTarget = this.target;
        Point pos = this.target.getLocation();
        this.offset = new Point(pos.x - point.x, pos.y - point.y);
        System.out.println("MOVE ACTION STARTED");
    }

    @Override
    boolean checkProgress() {
        if (this.event.isConsumed()) return false;
        this.parent.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_DRAGGED);
    }

    @Override
    void processPorgress() {
        this.point.translate(this.offset.x, this.offset.y);
        this.moveTarget.setLocation(this.point);
    }

    @Override
    boolean checkEnd() {
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_RELEASED);
    }

    @Override
    void processEnd() {
        super.processEnd();
        System.out.println("MOVE ACTION ENDED");
    }
}

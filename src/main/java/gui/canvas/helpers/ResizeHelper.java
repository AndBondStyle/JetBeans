package gui.canvas.helpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class ResizeHelper extends Helper {
    static int INNER_WIDTH = 6;
    static int OUTER_WIDTH = 0;
    static int CORNER_WIDTH = 8;

    static int[] LOCATIONS = {
            SwingConstants.NORTH, SwingConstants.EAST, SwingConstants.SOUTH, SwingConstants.WEST,
            SwingConstants.NORTH_WEST, SwingConstants.NORTH_EAST, SwingConstants.SOUTH_EAST, SwingConstants.SOUTH_WEST,
    };
    static int[] CURSORS = {
            Cursor.N_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR,
            Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
    };

    Component resizeTarget;
    Point offset;
    int mode;

    Rectangle rectFromPoints(int x1, int y1, int x2, int y2) {
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    Area combine(Rectangle a, Rectangle b, Rectangle c) {
        Area result = new Area();
        result.add(new Area(a));
        result.add(new Area(b));
        result.intersect(new Area(c));
        return result;
    }

    Shape[] computeAreas(Rectangle target) {
        // Outer rect bounds
        int outerLeft = target.x - OUTER_WIDTH;
        int outerTop = target.y - OUTER_WIDTH;
        int outerRight = target.x + target.width + OUTER_WIDTH;
        int outerBottom = target.y + target.height + OUTER_WIDTH;
        // Inner rect bounds
        int innerLeft = target.x + INNER_WIDTH;
        int innerTop = target.y + INNER_WIDTH;
        int innerRight = target.x + target.width - INNER_WIDTH;
        int innerBottom = target.y + target.height - INNER_WIDTH;
        // Side areas
        Rectangle north = rectFromPoints(outerLeft, outerTop, outerRight, innerTop);
        Rectangle east = rectFromPoints(innerRight, outerTop, outerRight, outerBottom);
        Rectangle south = rectFromPoints(outerLeft, innerBottom, outerRight, outerBottom);
        Rectangle west = rectFromPoints(outerLeft, outerTop, innerLeft, outerBottom);
        // Corner areas (initial)
        Rectangle nw = rectFromPoints(outerLeft, outerTop, outerLeft + CORNER_WIDTH, outerTop + CORNER_WIDTH);
        Rectangle ne = rectFromPoints(outerRight - CORNER_WIDTH, outerTop, outerRight, outerTop + CORNER_WIDTH);
        Rectangle se = rectFromPoints(outerRight - CORNER_WIDTH, outerBottom - CORNER_WIDTH, outerRight, outerBottom);
        Rectangle sw = rectFromPoints(outerLeft, outerBottom - CORNER_WIDTH, outerLeft + CORNER_WIDTH, outerBottom);
        // Finalize corner areas
        Area northWest = combine(north, west, nw);
        Area northEast = combine(north, east, ne);
        Area southEast = combine(south, east, se);
        Area southWest = combine(south, west, sw);
        return new Shape[]{north, east, south, west, northWest, northEast, southEast, southWest};
    }

    Point getOrigin(Rectangle target) {
        int left = target.x;
        int top = target.y;
        int right = target.x + target.width;
        int bottom = target.y + target.height;
        switch (LOCATIONS[mode]) {
            case SwingConstants.NORTH_WEST:
            case SwingConstants.NORTH:
                return new Point(left, top);
            case SwingConstants.NORTH_EAST:
            case SwingConstants.EAST:
                return new Point(right, top);
            case SwingConstants.SOUTH_EAST:
            case SwingConstants.SOUTH:
                return new Point(right, bottom);
            case SwingConstants.SOUTH_WEST:
            case SwingConstants.WEST:
                return new Point(left, bottom);
        }
        return null;
    }

    Rectangle getNewBounds(Rectangle oldBounds) {
        int x = point.x;
        int y = point.y;
        int left = oldBounds.x;
        int top = oldBounds.y;
        int right = oldBounds.x + oldBounds.width;
        int bottom = oldBounds.y + oldBounds.height;
        switch (LOCATIONS[mode]) {
            case SwingConstants.NORTH_WEST:
                return rectFromPoints(x, y, right, bottom);
            case SwingConstants.NORTH:
                return rectFromPoints(left, y, right, bottom);
            case SwingConstants.NORTH_EAST:
                return rectFromPoints(left, y, x, bottom);
            case SwingConstants.EAST:
                return rectFromPoints(left, top, x, bottom);
            case SwingConstants.SOUTH_EAST:
                return rectFromPoints(left, top, x, y);
            case SwingConstants.SOUTH:
                return rectFromPoints(left, top, right, y);
            case SwingConstants.SOUTH_WEST:
                return rectFromPoints(x, top, right, y);
            case SwingConstants.WEST:
                return rectFromPoints(x, top, right, bottom);
        }
        return null;
    }

    @Override
    boolean checkPossible() {
        if (this.event.isConsumed() || this.target == null) return false;
        Shape[] areas = computeAreas(this.target.getBounds());
        this.mode = -1;
        for (int i = 0; i < LOCATIONS.length; i++) {
            if (areas[i].contains(this.event.getPoint())) this.mode = i;
        }
        return this.mode != -1;
    }

    @Override
    void processPossible() {
        this.parent.cursor = Cursor.getPredefinedCursor(CURSORS[this.mode]);
    }

    @Override
    boolean checkStart() {
        if (!checkPossible()) return false;
        this.event.consume();
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_PRESSED);
    }

    @Override
    void processStart() {
        super.processStart();
        this.event.consume();
        Point origin = getOrigin(this.target.getBounds());
        this.offset = new Point(origin.x - point.x, origin.y - point.y);
        this.resizeTarget = this.target;
        System.out.println("RESIZE ACTION STARTED");
    }

    @Override
    boolean checkProgress() {
        if (this.event.isConsumed()) return false;
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_DRAGGED);
    }

    @Override
    void processPorgress() {
        this.event.consume();
        this.parent.cursor = Cursor.getPredefinedCursor(CURSORS[this.mode]);
        this.point.translate(this.offset.x, this.offset.y);
        Rectangle oldBounds = this.resizeTarget.getBounds();
        Rectangle newBounds = getNewBounds(oldBounds);
        Dimension size = newBounds.getSize();
        Dimension minSize = this.resizeTarget.getMinimumSize();
        Point oldOrigin = getOrigin(oldBounds);
        if (size.width < minSize.width) this.point.x = oldOrigin.x;
        if (size.height < minSize.height) this.point.y = oldOrigin.y;
        newBounds = getNewBounds(oldBounds);
        this.resizeTarget.setBounds(newBounds);
        this.resizeTarget.revalidate();
    }

    @Override
    boolean checkEnd() {
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_RELEASED);
    }

    @Override
    void processEnd() {
        super.processEnd();
        System.out.println("RESIZE ACTION ENDED");
    }

    //    @Override
//    public void process(MouseEvent e, CanvasItem item) {
//        super.process(e, item);
//        if (!active) {
//            if (item == null) return;
//            Rectangle targetRect = ((Component) item).getBounds();
//            Shape[] areas = computeAreas(targetRect);
//            mode = -1;
//            for (int i = 0; i < LOCATIONS.length; i++) {
//                if (areas[i].contains(e.getPoint())) mode = i;
//            }
//            if (mode == -1) return;
//            parent.cursor = Cursor.getPredefinedCursor(CURSORS[mode]);
//            e.consume();
//            if (e.getButton() == MouseEvent.BUTTON1 && e.getID() == MouseEvent.MOUSE_PRESSED) {
//                Point origin = getOrigin(targetRect);
//                this.offset = new Point(origin.x - point.x, origin.y - point.y);
//                this.resizeTarget = (Component) item;
//                this.active = true;
//            }
//        } else {
//            if (e.getButton() == MouseEvent.BUTTON1 && e.getID() == MouseEvent.MOUSE_RELEASED) active = false;
//            if (e.getID() != MouseEvent.MOUSE_DRAGGED) return;
//            e.consume();
//            parent.cursor = Cursor.getPredefinedCursor(CURSORS[mode]);
//            point.translate(offset.x, offset.y);
//            Rectangle oldBounds = this.resizeTarget.getBounds();
//            Rectangle newBounds = getNewBounds(oldBounds);
//            Dimension size = newBounds.getSize();
//            Dimension minSize = this.resizeTarget.getMinimumSize();
//            Point oldOrigin = getOrigin(oldBounds);
//            if (size.width < minSize.width) point.x = oldOrigin.x;
//            if (size.height < minSize.height) point.y = oldOrigin.y;
//            newBounds = getNewBounds(oldBounds);
//            this.resizeTarget.setBounds(newBounds);
//            this.resizeTarget.revalidate();
//        }
//    }
}

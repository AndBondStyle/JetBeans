package gui.canvas.helpers;

import gui.canvas.CanvasItem;
import gui.canvas.helpers.base.ToggleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ResizeHelper extends ToggleHelper {
    // https://imgur.com/a/VsWkIb7
    static int INNER_WIDTH = 6;
    static int OUTER_WIDTH = 0;
    static int CORNER_WIDTH = 8;

    static int[] LOCATIONS = {
            SwingConstants.NORTH, SwingConstants.EAST, SwingConstants.SOUTH, SwingConstants.WEST,
            SwingConstants.NORTH_WEST, SwingConstants.NORTH_EAST, SwingConstants.SOUTH_EAST, SwingConstants.SOUTH_WEST,
    };
    static Cursor[] CURSORS = Arrays.stream(new int[]{
            Cursor.N_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR,
            Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
    }).mapToObj(Cursor::getPredefinedCursor).toArray(Cursor[]::new);

    public Component resizeTarget;  // Target component
    public Point offset;            // Offset from origin point (see getOrigin)
    public int mode;                // Aka index of LOCATIONS / CURSORS element

    // TODO: Move to utilities
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

    // Gets point that should remain stationary during resize
    Point getOrigin(Rectangle target) {
        // TODO: Utility function: rect -> [top, left, right, bottom]
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
        // TODO: Utility function: rect -> [top, left, right, bottom]
        int left = oldBounds.x;
        int top = oldBounds.y;
        int right = oldBounds.x + oldBounds.width;
        int bottom = oldBounds.y + oldBounds.height;
        switch (LOCATIONS[mode]) {
            case SwingConstants.NORTH_WEST:
                return rectFromPoints(point.x, point.y, right, bottom);
            case SwingConstants.NORTH:
                return rectFromPoints(left, point.y, right, bottom);
            case SwingConstants.NORTH_EAST:
                return rectFromPoints(left, point.y, point.x, bottom);
            case SwingConstants.EAST:
                return rectFromPoints(left, top, point.x, bottom);
            case SwingConstants.SOUTH_EAST:
                return rectFromPoints(left, top, point.x, point.y);
            case SwingConstants.SOUTH:
                return rectFromPoints(left, top, right, point.y);
            case SwingConstants.SOUTH_WEST:
                return rectFromPoints(point.x, top, right, point.y);
            case SwingConstants.WEST:
                return rectFromPoints(point.x, top, right, bottom);
        }
        return null;
    }

    @Override
    public Cursor getCursor() {
        return this.possible || this.active ? CURSORS[this.mode] : null;
    }

    @Override
    public boolean isPossible() {
        if (this.event.isConsumed() || this.target == null) return false;
        if (!((CanvasItem) this.target).isResizable()) return false;
        Shape[] areas = computeAreas(this.target.getBounds());
        this.mode = IntStream.range(0, 8).map(i -> 7 - i)
                .filter(i -> areas[i].contains(this.event.getPoint()))
                .findFirst().orElse(-1);
        return this.mode != -1;
    }

    @Override
    public boolean isStarted() {
        if (!checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_PRESSED)) return false;
        this.event.consume();
        Point origin = getOrigin(this.target.getBounds());
        this.offset = new Point(origin.x - point.x, origin.y - point.y);
        this.resizeTarget = this.target;
        return true;
    }

    @Override
    public void handleProgress() {
        if (this.event.isConsumed()) return;
        if (!checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_DRAGGED)) return;
        this.event.consume();
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
    public boolean isEnded() {
        return checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_RELEASED);
    }
}

package gui.link;

import javax.swing.*;
import java.awt.*;

public class LinkEnd {
    static double CONTROL_SHIFT_RATIO = 0.5;

    Link parent;
    LinkManager manager = null;
    int orientation = -1;
    Point point = new Point();
    Point control = new Point();

    public LinkEnd(Link parent) {
        this.parent = parent;
    }

    public LinkEnd opposite() {
        if (this == parent.ends[0]) return parent.ends[1];
        return parent.ends[0];
    }

    public void arrange(Point point, int orientation, int distance) {
        this.orientation = orientation;
        this.point = point;
        int shift = (int) Math.round(distance * CONTROL_SHIFT_RATIO);
        if (orientation == SwingConstants.NORTH) this.control = new Point(point.x, point.y - shift);
        if (orientation == SwingConstants.EAST) this.control = new Point(point.x + shift, point.y);
        if (orientation == SwingConstants.SOUTH) this.control = new Point(point.x, point.y + shift);
        if (orientation == SwingConstants.WEST) this.control = new Point(point.x - shift, point.y);
        this.parent.update();
    }
}

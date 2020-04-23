package gui.link;

import javax.swing.*;
import java.awt.*;

public class LinkEnd {
    static int CONTROL_SHIFT = 100;

    Link parent;
    Point point = new Point();
    Point control = new Point();

    public LinkEnd(Link parent) {
        this.parent = parent;
    }

    public void arrange(Point point, int orientation) {
        this.point = point;
        if (orientation == SwingConstants.NORTH) this.control = new Point(point.x, point.y - CONTROL_SHIFT);
        if (orientation == SwingConstants.EAST) this.control = new Point(point.x + CONTROL_SHIFT, point.y);
        if (orientation == SwingConstants.SOUTH) this.control = new Point(point.x, point.y + CONTROL_SHIFT);
        if (orientation == SwingConstants.WEST) this.control = new Point(point.x - CONTROL_SHIFT, point.y);
        this.parent.update();
    }
}

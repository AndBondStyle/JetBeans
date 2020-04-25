package gui.link;

import javax.swing.*;
import java.awt.*;

public class LinkEnd {
    // TODO: Move to constants
    static double CONTROL_SHIFT_RATIO = 0.5;

    Link parent;
    LinkManager manager = null;
    int orientation = -1;
    int distance = -1;
    Point point = new Point();
    Point control = new Point();

    public LinkEnd(Link parent) {
        this.parent = parent;
    }

    public LinkEnd opposite() {
        return this == this.parent.ends[0] ? this.parent.ends[1] : this.parent.ends[0];
    }

    public void arrange(Point point, Integer orientation, Integer distance) {
        // Backup old values
        Point oldPoint = this.point;
        int oldOrientation = this.orientation;
        int oldDistance = this.distance;
        // Load new ones
        if (point != null) this.point = point;
        if (orientation != null) this.orientation = orientation;
        if (distance != null) this.distance = distance;
        // Check for difference
        if (oldPoint.equals(this.point) && oldOrientation == this.orientation && oldDistance == this.distance) return;
        // Something changed -> updating...
        int shift = (int) Math.round(this.distance * CONTROL_SHIFT_RATIO);
        if (this.orientation == SwingConstants.NORTH) this.control = new Point(this.point.x, this.point.y - shift);
        if (this.orientation == SwingConstants.EAST) this.control = new Point(this.point.x + shift, this.point.y);
        if (this.orientation == SwingConstants.SOUTH) this.control = new Point(this.point.x, this.point.y + shift);
        if (this.orientation == SwingConstants.WEST) this.control = new Point(this.point.x - shift, this.point.y);
        this.parent.update();
    }
}

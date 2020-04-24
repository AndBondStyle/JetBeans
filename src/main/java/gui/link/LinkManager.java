package gui.link;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LinkManager extends ComponentAdapter {
    List<LinkEnd> linkEnds = new ArrayList<>();
    boolean updated = false;
    Component parent;

    public LinkManager(Component parent) {
        this.parent = parent;
        this.parent.addComponentListener(this);
    }

    void assignOrigins() {
        List<LinkEnd> north = new ArrayList<>();
        List<LinkEnd> east = new ArrayList<>();
        List<LinkEnd> south = new ArrayList<>();
        List<LinkEnd> west = new ArrayList<>();

        Rectangle bounds = this.parent.getBounds();
        for (LinkEnd le : linkEnds) {
            Rectangle otherBounds = le.opposite().manager.parent.getBounds();
            int location = getLocation(bounds, otherBounds);
            if (location == 0) north.add(le);
            if (location == 1) east.add(le);
            if (location == 2) south.add(le);
            if (location == 3) west.add(le);
        }

        Point northCenter = new Point((int) bounds.getCenterX(), (int) bounds.getMinY());
        Point eastCenter = new Point((int) bounds.getMaxX(), (int) bounds.getCenterY());
        Point southCenter = new Point((int) bounds.getCenterX(), (int) bounds.getMaxY());
        Point westCenter = new Point((int) bounds.getMinX(), (int) bounds.getCenterY());

        for (LinkEnd le : north) le.arrange(northCenter, SwingUtilities.NORTH, null);
        for (LinkEnd le : east) le.arrange(eastCenter, SwingUtilities.EAST, null);
        for (LinkEnd le : south) le.arrange(southCenter, SwingUtilities.SOUTH, null);
        for (LinkEnd le : west) le.arrange(westCenter, SwingUtilities.WEST, null);
    }

    int getLocation(Rectangle a, Rectangle b) {
        Line2D line = new Line2D.Double(a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
        double left = a.getMinX();
        double top = a.getMinY();
        double right = a.getMaxX();
        double bottom = a.getMaxY();
        if (line.intersectsLine(new Line2D.Double(left, top, right, top))) return 0;
        if (line.intersectsLine(new Line2D.Double(right, top, right, bottom))) return 1;
        if (line.intersectsLine(new Line2D.Double(right, bottom, left, bottom))) return 2;
        if (line.intersectsLine(new Line2D.Double(left, bottom, left, top))) return 3;
        return -1;
    }

    void assignDistance(LinkEnd le) {
        switch (le.orientation) {
            case SwingConstants.NORTH:
            case SwingConstants.SOUTH:
                le.arrange(null, null, Math.abs(le.opposite().point.y - le.point.y));
                break;
            case SwingConstants.EAST:
            case SwingConstants.WEST:
                le.arrange(null, null, Math.abs(le.opposite().point.x - le.point.x));
                break;
        }
    }

    void update() {
        this.assignOrigins();
        HashSet<LinkManager> managers = new HashSet<>();
        for (LinkEnd le : linkEnds) managers.add(le.opposite().manager);
        for (LinkManager manager : managers) manager.updated = false;
        for (LinkManager manager : managers) {
            if (manager.updated) return;
            manager.assignOrigins();
            manager.updated = true;
        }
        for (LinkEnd le : linkEnds) {
            assignDistance(le);
            assignDistance(le.opposite());
        }
    }

    public void add(LinkEnd linkEnd) {
        this.linkEnds.add(linkEnd);
        linkEnd.manager = this;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        update();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        update();
    }
}
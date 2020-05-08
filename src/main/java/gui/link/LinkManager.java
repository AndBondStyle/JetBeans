package gui.link;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class LinkManager extends ComponentAdapter {
    public List<LinkEnd> linkEnds = new ArrayList<>();
    public boolean updated = false;
    public Component parent;

    public LinkManager(Component parent) {
        this.parent = parent;
        this.parent.addComponentListener(this);
    }

    void assignOrigins() {
        // Prepare arrays for each side
        List<LinkEnd> north = new ArrayList<>();
        List<LinkEnd> east = new ArrayList<>();
        List<LinkEnd> south = new ArrayList<>();
        List<LinkEnd> west = new ArrayList<>();
        // Distribute links to proper sides
        Rectangle bounds = this.parent.getBounds();
        for (LinkEnd le : this.linkEnds) {
            Rectangle otherBounds = le.opposite().manager.parent.getBounds();
            int location = getLocation(bounds, otherBounds);
            if (location == 0) north.add(le);
            if (location == 1) east.add(le);
            if (location == 2) south.add(le);
            if (location == 3) west.add(le);
        }
        // Distribute within sides
        this.distribute(bounds, north, SwingUtilities.NORTH);
        this.distribute(bounds, east, SwingUtilities.EAST);
        this.distribute(bounds, south, SwingUtilities.SOUTH);
        this.distribute(bounds, west, SwingUtilities.WEST);
    }

    void distribute(Rectangle bounds, List<LinkEnd> ends, int orientation) {
        if (orientation == SwingUtilities.NORTH || orientation == SwingUtilities.SOUTH) {
            ends.sort(Comparator.comparing(le -> le.opposite().manager.parent.getBounds().getCenterX()));
            double step = bounds.getWidth() / (ends.size() + 1);
            double y = orientation == SwingUtilities.NORTH ? bounds.getMinY() : bounds.getMaxY();
            double x = bounds.getMinX() + step;
            for (LinkEnd le : ends) {
                le.arrange(new Point((int) x, (int) y), orientation, null);
                x += step;
            }
        } else {
            ends.sort(Comparator.comparing(le -> le.opposite().manager.parent.getBounds().getCenterY()));
            double step = bounds.getHeight() / (ends.size() + 1);
            double x = orientation == SwingUtilities.WEST ? bounds.getMinX() : bounds.getMaxX();
            double y = bounds.getMinY() + step;
            for (LinkEnd le : ends) {
                le.arrange(new Point((int) x, (int) y), orientation, null);
                y += step;
            }
        }
    }

    int getLocation(Rectangle a, Rectangle b) {
        // TODO: Think about helper functions
        Line2D line = new Line2D.Double(a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
        double left = a.getMinX();
        double top = a.getMinY();
        double right = a.getMaxX();
        double bottom = a.getMaxY();
        // TODO: Works funky with corner-to-corner alignment
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

    public void update() {
        // Assign position of own links
        this.assignOrigins();
        // Get unique set of related (opposite) managers
        HashSet<LinkManager> managers = new HashSet<>();
        for (LinkEnd le : linkEnds) managers.add(le.opposite().manager);
        // Lazy-assign their positions too
        for (LinkManager manager : managers) manager.updated = false;
        for (LinkManager manager : managers) {
            if (manager.updated) return;
            manager.assignOrigins();
            manager.updated = true;
        }
        // Adjust link curves depending on distance
        for (LinkEnd le : linkEnds) {
            assignDistance(le);
            assignDistance(le.opposite());
        }
    }

    public void add(LinkEnd linkEnd) {
        if (linkEnd.manager != null) {
            linkEnd.manager.linkEnds.remove(linkEnd);
            linkEnd.manager.update();
        }
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

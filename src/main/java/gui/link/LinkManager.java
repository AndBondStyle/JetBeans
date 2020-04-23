package gui.link;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class LinkManager extends ComponentAdapter {
    public List<LinkEnd> linkEnds = new ArrayList<>();
    Component parent;

    public LinkManager(Component parent) {
        this.parent = parent;
        this.parent.addComponentListener(this);
    }

    void update(boolean deep) {
        List<LinkEnd> north = new ArrayList<>();
        List<LinkEnd> east = new ArrayList<>();
        List<LinkEnd> south = new ArrayList<>();
        List<LinkEnd> west = new ArrayList<>();

        Rectangle bounds = this.parent.getBounds();
        for (LinkEnd le : linkEnds) {
            Rectangle otherBounds = le.parent.getBounds();
            int location = getLocation(bounds, otherBounds);
            if (location == 0) north.add(le);
            if (location == 1) east.add(le);
            if (location == 2) south.add(le);
            if (location == 3) west.add(le);
        }

        // TODO: Fix govno (x inf)
        Point northCenter = new Point((int) bounds.getCenterX(), (int) bounds.getMinY());
        Point eastCenter = new Point((int) bounds.getMaxX(), (int) bounds.getCenterY());
        Point southCenter = new Point((int) bounds.getCenterX(), (int) bounds.getMaxY());
        Point westCenter = new Point((int) bounds.getMinX(), (int) bounds.getCenterY());

        if (!deep) {
            for (LinkEnd le : north) le.arrange(northCenter, SwingUtilities.NORTH, 0);
            for (LinkEnd le : east) le.arrange(eastCenter, SwingUtilities.EAST, 0);
            for (LinkEnd le : south) le.arrange(southCenter, SwingUtilities.SOUTH, 0);
            for (LinkEnd le : west) le.arrange(westCenter, SwingUtilities.WEST, 0);
        } else {
            multiArrange(north, northCenter, SwingUtilities.NORTH);
            multiArrange(east, eastCenter, SwingUtilities.EAST);
            multiArrange(south, southCenter, SwingUtilities.SOUTH);
            multiArrange(west, westCenter, SwingUtilities.WEST);
        }
    }

    void multiArrange(List<LinkEnd> links, Point origin, int orientation) {
        for (LinkEnd le : links) {
            le.opposite().manager.update(false);
            le.orientation = orientation;
            le.arrange(origin, orientation, computeShift(le));
            le.opposite().arrange(le.opposite().point, le.opposite().orientation, computeShift(le.opposite()));
        }
    }

    int computeShift(LinkEnd le) {
        switch (le.orientation) {
            case SwingConstants.NORTH:
            case SwingConstants.SOUTH:
                return Math.abs(le.opposite().point.y - le.point.y);
            case SwingConstants.EAST:
            case SwingConstants.WEST:
                return Math.abs(le.opposite().point.x - le.point.x);
        }
        return 0;
    }

    int getLocation(Rectangle a, Rectangle b) {
        Line2D line = new Line2D.Double(a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
        Line2D[] sides = getSides(a);
        for (int i = 0; i < sides.length; i++) if (line.intersectsLine(sides[i])) return i;
        return -1;
    }

    Line2D[] getSides(Rectangle rect) {
        double left = rect.getMinX();
        double top = rect.getMinY();
        double right = rect.getMaxX();
        double bottom = rect.getMaxY();
        return new Line2D[]{
                new Line2D.Double(left, top, right, top),
                new Line2D.Double(right, top, right, bottom),
                new Line2D.Double(right, bottom, left, bottom),
                new Line2D.Double(left, bottom, left, top)
        };
    }

    public void add(LinkEnd linkEnd) {
        this.linkEnds.add(linkEnd);
        linkEnd.manager = this;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        update(true);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        update(true);
    }
}

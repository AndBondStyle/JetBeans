package gui.link;

import gui.canvas.CanvasItem;

import javax.swing.*;
import java.awt.*;

public class Link extends JPanel implements CanvasItem {
    static int THICKNESS = 2;
    static int CONTAINS_THRESHOLD = 8;

    public LinkEnd[] ends = {new LinkEnd(this), new LinkEnd(this)};
    boolean isSelected = false;
    Point[] points = {};
    int[] xPoints = {};
    int[] yPoints = {};
    Color color;

    public Link(Color color) {
        this.color = color;
    }

    public boolean isResizable() {
        return false;
    }

    public boolean isMovable() {
        return true;
    }

    public boolean isSelectable() {
        return true;
    }

    public boolean isDeletable() {
        return true;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        this.repaint();
    }

    public void update() {
        Point a = ends[0].point;
        Point b = ends[1].point;
        int left = Math.min(a.x, b.x) - THICKNESS;
        int right = Math.max(a.x, b.x) + THICKNESS;
        int top = Math.min(a.y, b.y) - THICKNESS;
        int bottom = Math.max(a.y, b.y) + THICKNESS;
        setBounds(left, top, right - left, bottom - top);

        Point[] rawPoints = CubicBezier.getPoints(ends[0].point, ends[0].control, ends[1].control, ends[1].point);
        this.points = new Point[rawPoints.length];
        this.xPoints = new int[rawPoints.length];
        this.yPoints = new int[rawPoints.length];
        for (int i = 0; i < points.length; i++) {
            Point mapped = SwingUtilities.convertPoint(getParent(), rawPoints[i], this);
            this.points[i] = mapped;
            this.xPoints[i] = mapped.x;
            this.yPoints[i] = mapped.y;
        }

        this.repaint();
    }

    @Override
    public boolean contains(int x, int y) {
        if (!super.contains(x, y)) return false;
        for (Point point : this.points) {
            if (point.distance(x, y) <= CONTAINS_THRESHOLD) return true;
        }
        return false;
    }

    Color getColor() {
        if (isSelected) return this.color.brighter();
        return this.color;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // TODO: Something is very wrong with antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(THICKNESS));
        g2.setColor(getColor());
        g2.drawPolyline(this.xPoints, this.yPoints, this.points.length);
    }
}

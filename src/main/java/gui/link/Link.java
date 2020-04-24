package gui.link;

import gui.canvas.CanvasItem;

import java.awt.geom.CubicCurve2D;
import javax.swing.*;
import java.awt.*;

public class Link extends JPanel implements CanvasItem {
    static int THICKNESS = 2;
    static int CONTAINS_THRESHOLD = 8;

    public LinkEnd[] ends = {new LinkEnd(this), new LinkEnd(this)};
    boolean isSelected = false;
    CubicCurve2D curve = null;
    Point[] points = {};
    Color color;

    public Link(Color color) {
        this.color = color;
    }

    // CanvasItem implementation
    public boolean isResizable() {
        return false;
    }
    public boolean isMovable() {
        return false;
    }
    public boolean isSelectable() {
        return true;
    }
    public boolean isDeletable() {
        return true;
    }
    public int getPreferredLayer() { return JLayeredPane.PALETTE_LAYER; }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        this.repaint();
    }

    public void update() {
        Point start = ends[0].point;
        Point end = ends[1].point;
        int left = Math.min(start.x, end.x) - CONTAINS_THRESHOLD;
        int right = Math.max(start.x, end.x) + CONTAINS_THRESHOLD;
        int top = Math.min(start.y, end.y) - CONTAINS_THRESHOLD;
        int bottom = Math.max(start.y, end.y) + CONTAINS_THRESHOLD;
        setBounds(left, top, right - left, bottom - top);

        Point a = SwingUtilities.convertPoint(getParent(), ends[0].point, this);
        Point b = SwingUtilities.convertPoint(getParent(), ends[0].control, this);
        Point c = SwingUtilities.convertPoint(getParent(), ends[1].control, this);
        Point d = SwingUtilities.convertPoint(getParent(), ends[1].point, this);
        this.curve = new CubicCurve2D.Float(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
        this.points = CubicBezier.getPoints(a, b, c, d);
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(THICKNESS));
        g2.setColor(getColor());
        g2.draw(this.curve);
    }
}

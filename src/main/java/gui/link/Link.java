package gui.link;

import com.intellij.ui.ColorUtil;
import core.linking.LinkBase;
import gui.canvas.CanvasItem;

import java.awt.geom.CubicCurve2D;
import java.util.Arrays;

import javax.swing.*;
import java.awt.*;

public class Link extends JPanel implements CanvasItem {
    static int THICKNESS = 2;
    static int CONTAINS_THRESHOLD = 8;
    static int INACTIVE_ALPHA = 200;
    static int DASH_LENGTH = 10;
    static int UPDATE_DELAY = 100;
    static int UPDATE_STEP = 2;
    static int OFFSET_MOD = DASH_LENGTH * 2;

    public LinkEnd[] ends = {new LinkEnd(this), new LinkEnd(this)};
    public boolean isSelected = false;
    public CubicCurve2D curve = null;
    public Point[] points = {};
    public Color color;
    public LinkBase descriptor;
    public Timer timer;
    public int offset;

    public Link(Color color, LinkBase descriptor) {
        this.color = color;
        this.descriptor = descriptor;
        this.setOpaque(false);
        this.timer = new Timer(UPDATE_DELAY, (__) -> {
            this.offset = (this.offset + UPDATE_STEP) % OFFSET_MOD;
            this.repaint();
        });
        this.timer.start();
    }

    // CanvasItem implementation
    public boolean isResizable() {
        return false;
    }
    public boolean isMovable() {
        return false;
    }
    public boolean isSelectable() { return true; }
    public boolean isDeletable() {
        return true;
    }
    public int getPreferredLayer() { return JLayeredPane.PALETTE_LAYER; }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        this.timer.stop();
        if (selected) this.timer.start();
        this.repaint();
    }

    public void autoUpdate() {
        this.ends[0].manager.update();
        this.ends[1].manager.update();
    }

    public void detach() {
        for (LinkEnd end : this.ends) {
            if (end.manager != null) {
                end.manager.linkEnds.remove(end);
                end.manager.update();
                end.manager = null;
            }
        }
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
        if (!this.isVisible()) return false;
        if (!super.contains(x, y)) return false;
        return Arrays.stream(this.points).anyMatch(p -> p.distance(x, y) <= CONTAINS_THRESHOLD);
    }

    Color getColor() {
        Color color = this.isSelected ? this.color.brighter() : this.color;
        if (this.isSelected) return color;
        return ColorUtil.toAlpha(color, INACTIVE_ALPHA);
    }

    @Override
    public void paint(Graphics g) {
        if (this.curve == null || !this.isVisible()) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke stroke = this.isSelected
                ? new BasicStroke(
                    THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[] {DASH_LENGTH}, OFFSET_MOD - this.offset
                ) : new BasicStroke(THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);
        g2.setColor(getColor());
        g2.draw(this.curve);
    }
}

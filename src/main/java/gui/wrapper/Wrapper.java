package gui.wrapper;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsUtil;
import com.intellij.ui.ColorUtil;
import gui.canvas.CanvasItem;
import gui.link.LinkManager;
import gui.link.Link;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.JBColor;

import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;

public class Wrapper extends JBPanel<Wrapper> implements CanvasItem {
    // TODO: Move to constants
    static Color SEL_BORDER_COLOR = JBColor.CYAN;
    static int SEL_BORDER_WIDTH = 2;
    static int SEL_BORDER_EXTRA = 2;
    static int BG_STRIPES_WIDTH = 8;
    static int BG_STRIPES_SPACING = 12;
    static int BG_STRIPES_ALPHA = 128;

    Component target;
    public LinkManager linkManager;

    public Wrapper(Component target) {
        this.target = target;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(target, BorderLayout.CENTER);
        this.linkManager = new LinkManager(this);
        updateBorder(false);
    }

    public void attachLink(Link link, int end) {
        this.linkManager.add(link.ends[end]);
    }

    void updateBorder(boolean selected) {
        int extra = SEL_BORDER_EXTRA;
        int total = SEL_BORDER_WIDTH + SEL_BORDER_EXTRA;
        Border border = selected
                ? BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(extra, extra, extra, extra),
                    BorderFactory.createLineBorder(SEL_BORDER_COLOR, SEL_BORDER_WIDTH)
                ) : BorderFactory.createEmptyBorder(total, total, total, total);
        setBorder(border);
    }

    // CanvasItem implementation
    public boolean isResizable() { return true; }
    public boolean isMovable() {
        return true;
    }
    public boolean isSelectable() {
        return true;
    }
    public boolean isDeletable() {
        return true;
    }
    public int getPreferredLayer() { return JLayeredPane.MODAL_LAYER; }

    public void setSelected(boolean selected) {
        updateBorder(selected);
        repaint();
    }

    public Color getStripesColor() {
        Color base = EditorColorsUtil.getGlobalOrDefaultColor(EditorColors.VISUAL_INDENT_GUIDE_COLOR);
        return ColorUtil.toAlpha(base, BG_STRIPES_ALPHA);
    }

    public void drawStripes(Graphics2D g2) {
        int size = Math.max(this.getWidth(), this.getHeight()) * 2;
        int step = BG_STRIPES_WIDTH + BG_STRIPES_SPACING;
        int offset = SEL_BORDER_WIDTH + SEL_BORDER_EXTRA;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(BG_STRIPES_WIDTH));
        g2.setColor(this.getStripesColor());
        g2.setClip(offset, offset, this.getWidth() - offset * 2, this.getHeight() - offset * 2);
        for (int i = 0; i < size; i += step) g2.drawLine(0, i, i, 0);
        g2.setClip(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.drawStripes((Graphics2D) g);
        super.paintComponent(g);
    }

    public Dimension fixSize(Dimension size) {
        return new Dimension(
                size.width + (SEL_BORDER_WIDTH + SEL_BORDER_EXTRA) * 2,
                size.height + (SEL_BORDER_WIDTH + SEL_BORDER_EXTRA) * 2
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return fixSize(target.getPreferredSize());
    }

    @Override
    public Dimension getMinimumSize() {
        return fixSize(target.getMinimumSize());
    }

    @Override
    public Dimension getMaximumSize() {
        return fixSize(target.getMaximumSize());
    }
}

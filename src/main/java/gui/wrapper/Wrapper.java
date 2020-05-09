package gui.wrapper;

import gui.canvas.CanvasItem;
import gui.link.LinkManager;
import gui.link.Link;

import com.intellij.ui.JBColor;
import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;

public abstract class Wrapper extends JPanel implements CanvasItem {
    static Color SEL_BORDER_COLOR = JBColor.CYAN;
    static int SEL_BORDER_WIDTH = 2;
    static int SEL_BORDER_EXTRA = 2;

    public LinkManager linkManager = new LinkManager(this);
    public Object target;
    public Component view;

    public Wrapper(Object target) {
        this.target = target;
        this.initView();
    }

    public static Wrapper autowrap(Object object) {
        if (object instanceof Component) {
            return new GUIWrapper(object);
        } else {
            return new APIWrapper(object);
        }
    }

    protected void initView() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(this.view, BorderLayout.CENTER);
        this.setSelected(false);
        this.revalidate();
    };

    public void attachLink(Link link, int end) {
        this.linkManager.add(link.ends[end]);
    }

    public Object getTarget() {
        return this.target;
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

    public void setSelected(boolean selected) {
        updateBorder(selected);
        repaint();
    }

    private Dimension fixSize(Dimension size) {
        return new Dimension(
                size.width + (SEL_BORDER_WIDTH + SEL_BORDER_EXTRA) * 2,
                size.height + (SEL_BORDER_WIDTH + SEL_BORDER_EXTRA) * 2
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return fixSize(this.view.getPreferredSize());
    }

    @Override
    public Dimension getMinimumSize() {
        return fixSize(this.view.getMinimumSize());
    }

    @Override
    public Dimension getMaximumSize() {
        return fixSize(this.view.getMaximumSize());
    }
}

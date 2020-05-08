package gui.wrapper;

import gui.canvas.CanvasItem;
import gui.link.Link;
import gui.link.LinkManager;

import javax.swing.*;
import java.awt.*;

public class MockWrapper extends JPanel implements CanvasItem {
    public LinkManager linkManager = new LinkManager(this);

    public MockWrapper() {
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(20, 20));
    }

    public boolean isResizable() { return false; }
    public boolean isMovable() { return false; }
    public boolean isSelectable() { return false; }
    public void setSelected(boolean selected) {}
    public boolean isDeletable() { return false; }
    public int getPreferredLayer() { return JLayeredPane.MODAL_LAYER; }

    public void attachLink(Link link, int end) {
        this.linkManager.add(link.ends[end]);
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}

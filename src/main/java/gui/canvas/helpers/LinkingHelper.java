package gui.canvas.helpers;

import core.JetBeans;
import gui.canvas.Canvas;
import gui.canvas.CanvasItem;
import gui.canvas.helpers.base.Helper;
import gui.link.Link;
import gui.wrapper.MockWrapper;
import gui.wrapper.Wrapper;

import com.intellij.ui.JBColor;
import java.awt.event.MouseEvent;

public class LinkingHelper extends Helper {
    public MockWrapper boop = new MockWrapper();
    public Link link = new Link(JBColor.ORANGE, null) {
        public boolean isSelectable() { return false; }
    };
    public CanvasItem source = null;
    public boolean active = false;
    public JetBeans core;

    @Override
    public void setParent(Canvas parent) {
        super.setParent(parent);
        this.parent.addItem(this.boop);
        this.parent.addItem(this.link);
        this.link.setSelected(true);
        this.core = JetBeans.getInstance(this.parent.project);
        this.core.linker.addListener(e -> this.toggle(e.getActionCommand().equals("activate")));
        this.parent.addListener(e -> { if (e.getActionCommand().equals("select")) this.pin(); });
        this.toggle(false);
    }

    @Override
    public void process(MouseEvent e, CanvasItem item) {
        super.process(e, item);
        if (!this.active) return;
        this.link.setVisible(!(this.parent.selection == this.source && item == this.source));
        if (this.parent.selection == null || this.parent.selection == this.source) {
            this.point.translate(-this.boop.getWidth() / 2, -this.boop.getHeight() / 2);
            this.boop.setLocation(this.point);
        }
    }

    private void pin() {
        if (!this.active) return;
        CanvasItem selection = this.parent.selection;
        if (selection == null || selection == this.source) this.boop.attachLink(this.link, 1);
        else if (selection instanceof Wrapper) ((Wrapper) selection).attachLink(this.link, 1);
        this.link.autoUpdate();
    }

    private void toggle(boolean active) {
        this.active = active;
        this.link.setVisible(false);
        if (!this.active) {
            this.link.detach();
            return;
        }
        this.source = this.core.selection;
        if (!(this.source instanceof Wrapper)) return;
        ((Wrapper) this.source).attachLink(this.link, 0);
        this.boop.setLocation(((Wrapper) this.source).getLocation());
        this.boop.attachLink(this.link, 1);
        this.link.autoUpdate();
    }
}

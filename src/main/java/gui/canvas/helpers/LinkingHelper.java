package gui.canvas.helpers;

import com.intellij.ui.JBColor;
import core.JetBeans;
import gui.canvas.Canvas;
import gui.canvas.CanvasItem;
import gui.canvas.helpers.base.Helper;
import gui.link.Link;
import gui.wrapper.MockWrapper;
import gui.wrapper.Wrapper;

import java.awt.event.MouseEvent;

public class LinkingHelper extends Helper {
    public MockWrapper boop = new MockWrapper();
    public Link link = new Link(JBColor.ORANGE);
    public boolean active = false;
    public JetBeans core;

    @Override
    public void setParent(Canvas parent) {
        super.setParent(parent);
        this.parent.addItem(this.boop);
        this.parent.addItem(this.link);
        this.core = JetBeans.getInstance(this.parent.project);
        this.core.linker.addListener(x -> this.toggle(x.getActionCommand().equals("activate")));
        this.toggle(true);
    }

    @Override
    public void process(MouseEvent e, CanvasItem item) {
        super.process(e, item);
        this.point.translate(-this.boop.getWidth() / 2, -this.boop.getHeight() / 2);
        this.boop.setLocation(this.point);
    }

    private void toggle(boolean active) {
        this.active = active;
        this.link.setVisible(this.active);
        CanvasItem selection = this.core.selection;
        if (!(selection instanceof Wrapper)) return;
        this.link.detach();
        if (this.active) {
            ((Wrapper) selection).attachLink(this.link, 0);
            this.boop.attachLink(this.link, 1);
            this.link.ends[0].manager.update();
        }
    }
}

package core.linking;

import com.intellij.ui.JBColor;
import core.main.JetBeans;
import core.inspection.EventInfo;
import core.inspection.MethodInfo;
import core.inspection.PropertyInfo;
import gui.canvas.Canvas;
import gui.common.SimpleEventSupport;
import gui.link.Link;
import gui.wrapper.Wrapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Linker implements SimpleEventSupport {
    public JetBeans core;
    public boolean active = false;
    public Object source = null;
    public Object destination = null;

    public Linker(JetBeans core) {
        this.core = core;
    }

    public boolean isAcceptable(Object item) {
        if (!this.active) {
            if (item instanceof PropertyInfo) return ((PropertyInfo) item).isBound();
            return item instanceof EventInfo;
        } else {
            if (item instanceof PropertyInfo) {
                PropertyInfo prop = (PropertyInfo) item;
                return prop.isSettable() && !prop.equals(this.source);
            }
            return item instanceof MethodInfo;
        }
    }

    public void accept(Object item) {
        if (!active) {
            this.active = true;
            this.source = item;
            this.fireEvent("activate");
        } else {
            this.active = false;
            this.destination = item;
            this.link();
            this.fireEvent("deactivate");
        }
    }

    private void link() {
        if (this.destination != null) {
            LinkBase link = this.source instanceof PropertyInfo
                    ? new PropertyLink(this.core.project, ((PropertyInfo) this.source), this.destination)
                    : new EventLink(this.core.project, (EventInfo) this.source, this.destination);
            link.addListener(this.makeCallback(
                    link,
                    this.source instanceof PropertyInfo
                            ? ((PropertyInfo) this.source).target
                            : ((EventInfo) this.source).target,
                    this.destination instanceof PropertyInfo
                            ? ((PropertyInfo) this.destination).target
                            : ((MethodInfo) this.destination).target
            ));
            link.init(null);
        }
        this.source = null;
        this.destination = null;
    }

    private ActionListener makeCallback(LinkBase link, Object src, Object dst) {
        final Canvas canvas = this.core.getCanvas();
        if (canvas == null) return null;
        final Wrapper source = canvas.findWrapper(src);
        final Wrapper destination = canvas.findWrapper(dst);
        if (source == null || destination == null) return null;
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!e.getActionCommand().equals("created")) return;
                Link canvasLink = new Link(JBColor.CYAN, link);
                source.attachLink(canvasLink, 0);
                destination.attachLink(canvasLink, 1);
                canvas.addItem(canvasLink);
                canvas.setSelection(canvasLink);
                canvasLink.autoUpdate();
                SwingUtilities.invokeLater(() -> link.removeListener(this));
            }
        };
    }
}

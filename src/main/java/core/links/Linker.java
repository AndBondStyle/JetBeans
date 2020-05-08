package core.links;

import com.intellij.ui.JBColor;
import core.JetBeans;
import core.inspection.EventInfo;
import core.inspection.MethodInfo;
import core.inspection.PropertyInfo;
import gui.canvas.Canvas;
import gui.canvas.CanvasItem;
import gui.common.SimpleEventSupport;
import gui.link.Link;
import gui.wrapper.Wrapper;

import java.awt.event.ActionListener;

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
        Object source = this.source;
        Object destination = this.destination;
        this.source = null;
        this.destination = null;
        if (destination != null) {
            if (source instanceof PropertyInfo) {
                PropertyLink link = new PropertyLink(this.core.project, (PropertyInfo) source, destination);
                Object srcObject = ((PropertyInfo) source).target;
                Object dstObject = destination instanceof PropertyInfo ?
                        ((PropertyInfo) destination).target : ((MethodInfo) destination).target;
                link.addListener(this.makeCallback(srcObject, dstObject, link));
                link.init(null);
            } else {
                System.err.println("Not supported");
            }
        }
    }

    private ActionListener makeCallback(Object src, Object dst, Object link) {
        final Canvas canvas = this.core.getCanvas();
        final Wrapper source = canvas.findWrapper(src);
        final Wrapper destination = canvas.findWrapper(dst);
        if (source == null || destination == null) return null;
        return e -> {
            if (e.getActionCommand().equals("created")) {
                Link canvasLink = new Link(JBColor.MAGENTA, link);
                source.attachLink(canvasLink, 0);
                destination.attachLink(canvasLink, 1);
                canvas.addItem(canvasLink);
                canvas.setSelection(canvasLink);
                canvasLink.autoUpdate();
            }
        };
    }
}

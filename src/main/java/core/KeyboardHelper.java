package core;

import gui.canvas.Canvas;
import gui.canvas.CanvasItem;
import gui.link.Link;
import gui.link.LinkEnd;
import gui.wrapper.Wrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KeyboardHelper {
    public KeyboardFocusManager manager;
    public JetBeans core;

    public KeyboardHelper(JetBeans core) {
        this.manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        this.manager.addKeyEventDispatcher( e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) return this.keyPressed(e);
            return false;
        });
        this.core = core;
    }

    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_DELETE) return false;
        Component focus = this.manager.getFocusOwner();
        Canvas canvas = this.core.getCanvas();
        System.out.println("Event: " + e);
        System.out.println("Focus: " + focus);
        System.out.println("Canvas: " + canvas);
        System.out.println("Selection: " + this.core.selection);
        if (focus == null || canvas == null) return false;
        CanvasItem selection = canvas.getSelection();
        if (canvas.isAncestorOf(focus)) {
            System.out.println("REMOVE");
            if (selection instanceof Wrapper) this.removeWrapper((Wrapper) selection);
            if (selection instanceof Link) this.removeLink((Link) selection);
        }
        return true;
    }

    private void removeWrapper(Wrapper wrapper) {
        HashSet<Link> seen = new HashSet<>();
        List<LinkEnd> ends = new ArrayList<>(wrapper.linkManager.linkEnds);
        for (LinkEnd le : ends) {
            if (seen.contains(le.parent)) continue;
            this.removeLink(le.parent);
            seen.add(le.parent);
        }
        this.core.getCanvas().removeItem(wrapper);
    }

    private void removeLink(Link link) {
        link.descriptor.destroy();
        link.detach();
        this.core.getCanvas().removeItem(link);
    }
}

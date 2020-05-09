package core;

import gui.canvas.Canvas;
import gui.canvas.CanvasItem;

import java.awt.event.KeyEvent;
import java.awt.*;

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
        if (focus == null || canvas == null) return false;
        CanvasItem selection = canvas.getSelection();
        if (canvas.isAncestorOf(focus)) this.core.autoRemove(selection);
        return true;
    }
}

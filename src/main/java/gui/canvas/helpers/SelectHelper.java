package gui.canvas.helpers;

import gui.canvas.CanvasItem;

import java.awt.event.MouseEvent;

public class SelectHelper extends Helper {
    public void process(MouseEvent e, CanvasItem item) {
        super.process(e, item);
        if (e.isConsumed()) return;
        if (item != null && item.isSelectable() && checkEvent(null, MouseEvent.MOUSE_PRESSED))
            this.parent.select(item);
        if (item == null && checkEvent(MouseEvent.BUTTON1, MouseEvent.MOUSE_CLICKED))
            this.parent.select(null);
    }
}

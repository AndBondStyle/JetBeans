package gui.canvas;

import gui.common.SimpleEventSupport;
import gui.canvas.helpers.base.Helper;
import gui.canvas.helpers.*;

import com.intellij.ui.components.JBScrollPane;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;

// Main canvas widget, handling move/select/resize actions
public class Canvas extends JPanel implements SimpleEventSupport {
    // TODO: Move to constants
    static Dimension CANVAS_SIZE = new Dimension(5000, 5000);
    static Dimension MIN_ITEM_SIZE = new Dimension(20, 20);

    public Content content;
    public JBScrollPane scroll;
    public CanvasItem selection = null;

    public Cursor cursor;
    public Helper[] helpers = {
            new PanHelper(),
            new SelectHelper(),
            new MoveHelper(),
            new ResizeHelper(),
    };

    public Canvas() {
        this.content = new Content(this);
        this.content.setPreferredSize(CANVAS_SIZE);
        this.scroll = new JBScrollPane(this.content);
        this.setLayout(new BorderLayout());
        this.add(this.scroll, BorderLayout.CENTER);
        for (Helper helper : this.helpers) helper.setParent(this);
    }

    public void addItem(CanvasItem item) {
        Component comp = (Component) item;
        this.content.add(comp);
        this.content.setLayer(comp, item.getPreferredLayer());
        this.content.revalidate();
        Dimension size = comp.getPreferredSize();
        // Expand element if preferred size is too small
        if (size.width < MIN_ITEM_SIZE.width) size.width = MIN_ITEM_SIZE.width;
        if (size.height < MIN_ITEM_SIZE.height) size.height = MIN_ITEM_SIZE.height;
        comp.setSize(size);
    }

    public void processMouseEvent(MouseEvent e) {
        // Get root item (direct child of this.content)
        Component item = this.content.getTargetItem(e);
        Component target = null;
        if (item != null) {
            // Find actual event target - deepest child containing event point
            Point p = SwingUtilities.convertPoint(this.content, e.getPoint(), item);
            target = SwingUtilities.getDeepestComponentAt(item, p.x, p.y);
        }
        // Notify helpers
        this.cursor = null;
        for (Helper helper : this.helpers) helper.process(e, (CanvasItem) item);
        if (target != null && !e.isConsumed()) {
            // If event wasn't consumed, forward it to original target
            MouseEvent ee = SwingUtilities.convertMouseEvent(this.content, e, target);
            target.dispatchEvent(ee);
        }
        // Update glass cursor (if modified)
        if (this.cursor == null && target != null) this.cursor = target.getCursor();
        if (this.cursor == null) this.cursor = Cursor.getDefaultCursor();
        if (this.cursor != this.content.glass.getCursor()) this.content.glass.setCursor(this.cursor);
    }

    public void setSelection(CanvasItem target) {
        if (target == this.selection) return;
        // Disable previous item selection
        if (this.selection != null) this.selection.setSelected(false);
        if (target != null) {
            // Highlight new item + make topmost in current layer
            target.setSelected(true);
            this.content.moveToFront((Component) target);
        }
        this.selection = target;
        // Void selected -> grab focus
        if (this.selection == null) this.content.requestFocus();
        this.fireEvent("select");
    }

    public CanvasItem getSelection() {
        return this.selection;
    }
}

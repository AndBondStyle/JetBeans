package gui.canvas;

import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import gui.canvas.helpers.*;
import gui.wrapper.Wrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

// Main canvas widget, handling move/select/resize actions
public class Canvas extends JBPanel<Canvas> {
    static Dimension CANVAS_SIZE = new Dimension(5000, 5000);

    public Content content;
    public JBScrollPane scroll;
    public CanvasItem selection = null;

    public Cursor cursor;
    public Helper[] helpers = {
            new PanHelper(),
            new SelectHelper(),
            new ResizeHelper(),
            new MoveHelper(),
    };

    public Canvas() {
        content = new Content(this);
        content.setPreferredSize(CANVAS_SIZE);
        scroll = new JBScrollPane(content);
        this.setLayout(new BorderLayout());
        this.add(scroll, BorderLayout.CENTER);
        for (Helper helper : helpers) helper.parent = this;

        // TODO: TEMP
        content.setLayout(null);
        JComponent temp = new JBIntSpinner(50, 0, 100, 1);
        Wrapper wrapper = new Wrapper(temp);
        content.add(wrapper, JLayeredPane.DEFAULT_LAYER);
        wrapper.setSize(wrapper.fixSize(temp.getPreferredSize()));
        wrapper.setLocation(100, 100);
    }

    public void processMouseEvent(MouseEvent e) {
        Component item = content.getTargetItem(e);
        this.cursor = Cursor.getDefaultCursor();
        Component target = null;
        if (item != null) {
            Point p = SwingUtilities.convertPoint(content.glass, e.getPoint(), item);
            target = SwingUtilities.getDeepestComponentAt(item, p.x, p.y);
            if (target != null) this.cursor = target.getCursor();
        }
        for (Helper helper : helpers) helper.process(e, (CanvasItem) item);
        if (target != null && !e.isConsumed()) {
            MouseEvent ee = SwingUtilities.convertMouseEvent(content, e, target);
            target.dispatchEvent(ee);
        }
        if (this.cursor != content.glass.getCursor()) content.glass.setCursor(this.cursor);
    }

    public void select(CanvasItem target) {
        if (target == this.selection) return;
        if (this.selection != null) this.selection.setSelected(false);
        if (target != null) (target).setSelected(true);
        this.selection = target;
        if (this.selection == null) content.requestFocus();
    }
}

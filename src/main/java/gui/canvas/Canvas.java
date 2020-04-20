package gui.canvas;

import com.intellij.ui.JBColor;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBLayeredPane;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import gui.common.Selectable;
import gui.wrapper.Wrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// Main canvas widget, handling move/select/resize actions
public class Canvas extends JBPanel<Canvas> implements MouseListener, MouseMotionListener {
    static Dimension CANVAS_SIZE = new Dimension(5000, 5000);

    JBLayeredPane layers = new JBLayeredPane();
    JBScrollPane scroll = new JBScrollPane();
    Content content = new Content();
    JPanel glass = new JPanel();

    Component selection = null;
    Point previous = null;
    boolean navigating = false;
    boolean moving = false;
    boolean resizing = false;

    public Canvas() {
        content.setFocusable(true);
        glass.setOpaque(false);
        glass.setFocusable(false);
        glass.addMouseListener(this);
        glass.addMouseMotionListener(this);

        layers.setBorder(BorderFactory.createLineBorder(JBColor.CYAN, 2));
        layers.add(content, JLayeredPane.DEFAULT_LAYER);
        layers.add(glass, JLayeredPane.PALETTE_LAYER);
        content.setBounds(new Rectangle(new Point(), CANVAS_SIZE));
        glass.setBounds(new Rectangle(new Point(), CANVAS_SIZE));
        layers.setPreferredSize(CANVAS_SIZE);

        scroll.setViewportView(layers);
        this.setLayout(new BorderLayout());
        this.add(scroll, BorderLayout.CENTER);

        // TODO: TEMP
        content.setLayout(null);
        JComponent temp = new JBIntSpinner(50, 0, 100, 1);
        Wrapper wrapper = new Wrapper(temp);
        content.add(wrapper);
        wrapper.setSize(temp.getPreferredSize());
        wrapper.setLocation(100, 100);
    }

    void setSelection(Component selection) {
        if (selection == this.selection) return;
        if (this.selection != null) ((Selectable) this.selection).setSelected(false);
        if (selection != null) ((Selectable) selection).setSelected(true);
        this.selection = selection;
        if (this.selection == null) content.requestFocus();
    }

    // Events section

    Component forward(MouseEvent e) {
        Component target = SwingUtilities.getDeepestComponentAt(content, e.getX(), e.getY());
        // Check if background is clicked
        if (target == content) target = null;
        // Convert & re-dispatch event
        if (target != null) {
            // Dispatch to real target
            MouseEvent ee = SwingUtilities.convertMouseEvent(content, e, target);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(ee);
            // Find our component (probably Wrapper or Link)
            while (target.getParent() != content) target = target.getParent();
            e = SwingUtilities.convertMouseEvent(content, e, target);
            // Check if component actually contains point
            if (!target.contains(e.getX(), e.getY())) target = null;
        }
        return target;
    }

    public void mouseClicked(MouseEvent e) {
        Component target = forward(e);
        if (e.getButton() == MouseEvent.BUTTON1) setSelection(target);
    }

    public void mousePressed(MouseEvent e) {
        Component target = forward(e);
        if (target != null && e.getButton() == MouseEvent.BUTTON1) {
            setSelection(target);
            moving = true;
        }
        if (target == null && e.getButton() == MouseEvent.BUTTON1) navigating = true;
        if (e.getButton() == MouseEvent.BUTTON2) navigating = true;
        previous = e.getPoint();
    }

    public void mouseReleased(MouseEvent e) {
        forward(e);
        navigating = false;
        moving = false;
        resizing = false;
    }

    public void mouseEntered(MouseEvent e) { forward(e); }

    public void mouseExited(MouseEvent e) { forward(e); }

    public void mouseMoved(MouseEvent e) { forward(e); }

    public void mouseDragged(MouseEvent e) {
        forward(e);
        Point delta = new Point(previous.x - e.getX(), previous.y - e.getY());
        if (navigating) {
            Point pos = scroll.getViewport().getViewPosition();
            pos.translate(delta.x, delta.y);
            JViewport viewport = scroll.getViewport();
            pos.x = Math.min(Math.max(pos.x, 0), CANVAS_SIZE.width - viewport.getWidth());
            pos.y = Math.min(Math.max(pos.y, 0), CANVAS_SIZE.height - viewport.getHeight());
            scroll.getViewport().setViewPosition(pos);
            previous = e.getPoint();
            previous.translate(delta.x, delta.y);
        } else if (moving) {
            Point pos = selection.getLocation();
            pos.translate(-delta.x, -delta.y);
            selection.setLocation(pos);
            previous = e.getPoint();
        } else {
            previous = e.getPoint();
        }
    }
}

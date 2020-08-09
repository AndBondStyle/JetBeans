package core;

import com.intellij.ui.JBColor;
import com.intellij.util.concurrency.SwingWorker;

import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;

public class TestBean extends JPanel {
    public Color color;
    public Timer timer;

    public TestBean() {
        this.color = Color.ORANGE;
        this.setPreferredSize(new Dimension(60, 40));
        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        this.setOpaque(false);
        this.timer = new Timer(1000, (__) -> this.toggle());
        this.timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.color);
        g2.fillArc(5, 5, 30, 30, 0, 360);
        g2.fillArc(25, 5, 30, 30, 0, 360);
        g2.fillRect(20, 5, 20, 30);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color newColor) {
        firePropertyChange("color", this.color, newColor);
        this.color = newColor;
        this.repaint();
    }

    public void toggle() {
        this.setColor(this.color == JBColor.ORANGE ? JBColor.GREEN : JBColor.ORANGE);
        this.repaint();
    }

    public void processEvent(AWTEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_CLICKED) this.toggle();
    }
}

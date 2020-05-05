package gui.common;

import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;

public class TestBean extends JPanel {
    private Color color;

    public TestBean() {
        this.color = Color.ORANGE;
        this.setPreferredSize(new Dimension(60, 40));
        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        this.setOpaque(false);
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

    public void processEvent(AWTEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_CLICKED) {
            if (this.color == Color.ORANGE) {
                this.setColor(Color.GREEN);
            } else {
                this.setColor(Color.ORANGE);
            }
        }
        this.repaint();
    }
}

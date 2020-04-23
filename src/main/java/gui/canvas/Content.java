package gui.canvas;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Content extends JLayeredPane {
    static int GRID_THICKNESS = 1;
    static int GRID_STEP = 50;

    public JPanel glass;

    public Content(Canvas parent) {
        glass = new JPanel() {
            protected void processEvent(AWTEvent e) {
                if (e instanceof MouseEvent) parent.processMouseEvent((MouseEvent) e);
            }
        };
        // Add mock listeners to enable processMouseEvent calls
        glass.addMouseListener(new MouseAdapter() {
        });
        glass.addMouseMotionListener(new MouseAdapter() {
        });
        glass.setOpaque(false);
        glass.setFocusable(false);

        this.setLayout(null);
        this.setOpaque(true);
        this.setFocusable(true);
        this.add(glass, JLayeredPane.DRAG_LAYER);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                glass.setBounds(new Rectangle(new Point(), getSize()));
            }
        });
    }

    Component getTargetItem(MouseEvent e) {
        if (!this.contains(e.getPoint())) return null;
        for (Component child : this.getComponents()) {
            // TODO: Selection priority
            if (child == glass) continue;
            Point p = SwingUtilities.convertPoint(glass, e.getPoint(), child);
            if (child.contains(p)) return child;
        }
        return null;
    }

    @Override
    public Color getBackground() {
        // Grid background color (same as code editor background)
        return EditorColorsUtil.getGlobalOrDefaultColorScheme().getDefaultBackground();
    }

    @Override
    public Color getForeground() {
        // Grid lines color (same as code editor vertical lines)
        return EditorColorsUtil.getGlobalOrDefaultColor(EditorColors.VISUAL_INDENT_GUIDE_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(this.getForeground());
        g2.setStroke(new BasicStroke(GRID_THICKNESS));
        for (int y = GRID_STEP; y < this.getHeight(); y += GRID_STEP) g2.drawLine(0, y, this.getWidth(), y);
        for (int x = GRID_STEP; x < this.getWidth(); x += GRID_STEP) g2.drawLine(x, 0, x, this.getHeight());
    }
}

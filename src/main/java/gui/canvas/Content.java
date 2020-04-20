package gui.canvas;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsUtil;
import com.intellij.ui.components.JBPanel;

import java.awt.*;

// Helper widget that actually holds canvas' contents
public class Content extends JBPanel<Content> {
    static int GRID_THICKNESS = 1;
    static int GRID_STEP = 50;

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
        // Fill background
        g2.setColor(this.getBackground());
        g2.fill(this.getVisibleRect());
        // Draw grid lines
        g2.setColor(this.getForeground());
        g2.setStroke(new BasicStroke(GRID_THICKNESS));
        for (int y = GRID_STEP; y < this.getHeight(); y += GRID_STEP) {
            // Vertical
            g2.drawLine(0, y, this.getWidth(), y);
        }
        for (int x = GRID_STEP; x < this.getWidth(); x += GRID_STEP) {
            // Horizontal
            g2.drawLine(x, 0, x, this.getHeight());
        }
    }
}

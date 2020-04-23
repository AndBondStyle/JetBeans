package gui.wrapper;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import gui.canvas.CanvasItem;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Wrapper extends JBPanel<Wrapper> implements CanvasItem {
    static Color SEL_BORDER_COLOR = JBColor.CYAN;
    static int SEL_BORDER_WIDTH = 2;
    static int SEL_BORDER_EXTRA = 2;

    Component target;

    public Wrapper(Component target) {
        this.target = target;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(target, BorderLayout.CENTER);
        updateBorder(false);
    }

    @Override
    public void setSelected(boolean selected) {
        updateBorder(selected);
        repaint();
    }

    void updateBorder(boolean selected) {
        int extra = SEL_BORDER_EXTRA;
        int total = SEL_BORDER_WIDTH + SEL_BORDER_EXTRA;
        Border border = selected
                ? BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(extra, extra, extra, extra),
                BorderFactory.createLineBorder(SEL_BORDER_COLOR, SEL_BORDER_WIDTH)
        )
                : BorderFactory.createEmptyBorder(total, total, total, total);
        setBorder(border);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

    public Dimension fixSize(Dimension size) {
        return new Dimension(
                size.width + (SEL_BORDER_WIDTH + SEL_BORDER_EXTRA) * 2,
                size.height + (SEL_BORDER_WIDTH + SEL_BORDER_EXTRA) * 2
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return fixSize(target.getPreferredSize());
    }

    @Override
    public Dimension getMinimumSize() {
        return fixSize(target.getMinimumSize());
    }

    @Override
    public Dimension getMaximumSize() {
        return fixSize(target.getMaximumSize());
    }
}

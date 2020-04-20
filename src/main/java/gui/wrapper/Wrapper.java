package gui.wrapper;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import gui.common.Selectable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Wrapper extends JBPanel<Wrapper> implements Selectable {
    static Color SELECTION_BORDER_COLOR = JBColor.CYAN;
    static int SELECTION_BORDER_WIDTH = 2;

    // Selectable interface impl.
    boolean isSelected = false;
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) {
        isSelected = selected;
        setBorder(getBorder());
        repaint();
    }

    Component target;

    public Wrapper(Component target) {
        this.target = target;
        this.setLayout(new BorderLayout());
        this.add(target, BorderLayout.CENTER);
        setSelected(false);
    }

    @Override
    public Border getBorder() {
        return isSelected
                ? BorderFactory.createLineBorder(SELECTION_BORDER_COLOR, SELECTION_BORDER_WIDTH)
                : BorderFactory.createLineBorder(new Color(0, 0, 0, 0), SELECTION_BORDER_WIDTH);
    }

    Dimension fixSize(Dimension size) {
        return new Dimension(
            size.width + SELECTION_BORDER_WIDTH * 2,
            size.height + SELECTION_BORDER_WIDTH * 2
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

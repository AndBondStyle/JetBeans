package gui.wrapper;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsUtil;
import com.intellij.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class GUIWrapper extends Wrapper {
    static int BG_STRIPES_WIDTH = 8;
    static int BG_STRIPES_SPACING = 12;
    static int BG_STRIPES_ALPHA = 128;

    public GUIWrapper(Object target) {
        super(target);
    }

    @Override
    protected void initView() {
        this.view = (Component) this.target;
        super.initView();
    }

    public Color getStripesColor() {
        Color base = EditorColorsUtil.getGlobalOrDefaultColor(EditorColors.VISUAL_INDENT_GUIDE_COLOR);
        return ColorUtil.toAlpha(base, BG_STRIPES_ALPHA);
    }

    public void drawStripes(Graphics2D g2) {
        int size = Math.max(this.getWidth(), this.getHeight()) * 2;
        int step = BG_STRIPES_WIDTH + BG_STRIPES_SPACING;
        int offset = SEL_BORDER_WIDTH + SEL_BORDER_EXTRA;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(BG_STRIPES_WIDTH));
        g2.setColor(this.getStripesColor());
        g2.setClip(offset, offset, this.getWidth() - offset * 2, this.getHeight() - offset * 2);
        for (int i = 0; i < size; i += step) g2.drawLine(0, i, i, 0);
        g2.setClip(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.drawStripes((Graphics2D) g);
        super.paintComponent(g);
    }
}

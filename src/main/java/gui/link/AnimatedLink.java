package gui.link;

import javax.swing.*;
import java.awt.*;

public class AnimatedLink extends Link {
    static int DASH_LENGTH = 10;
    static int UPDATE_DELAY = 100;
    static int UPDATE_STEP = 1;
    static int OFFSET_MOD = DASH_LENGTH * 2;

    public Timer timer;
    public int offset = 0;

    public AnimatedLink(Color color) {
        super(color, null);
        this.setSelected(true);
        this.timer = new Timer(UPDATE_DELAY, (__) -> {
            this.offset = (this.offset + UPDATE_STEP) % OFFSET_MOD;
            this.repaint();
        });
        this.timer.start();
    }

    public boolean isSelectable() { return false; }
    public boolean isDeletable() { return false; }

    @Override
    public void paint(Graphics g) {
        if (this.curve == null || !this.isVisible()) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(
                THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {DASH_LENGTH}, OFFSET_MOD - this.offset
        ));
        g2.setColor(getColor());
        g2.draw(this.curve);
    }
}

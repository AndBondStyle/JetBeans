package gui.inspector.editors;

import javax.swing.*;

public class ObjectEditor extends Editor {
    private JLabel label;

    @Override
    protected void populateCenterPanel() {
        this.label = new JLabel();
        this.centerPanel.add(Box.createHorizontalStrut(5));
        this.centerPanel.add(this.label);
    }

    @Override
    protected void update() {
        this.label.setText("" + this.value);
        this.parent.repaint();
    }
}

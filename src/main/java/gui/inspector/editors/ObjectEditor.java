package gui.inspector.editors;

import javax.swing.*;

public class ObjectEditor extends Editor {
    private JTextField text;

    @Override
    protected void populateCenterPanel() {
        this.text = new JTextField();
        this.text.setOpaque(false);
        this.text.setBorder(BorderFactory.createEmptyBorder());
        this.text.setEnabled(false);
        this.centerPanel.add(this.text);
    }

    @Override
    protected void update() {
        this.text.setText("" + this.value);
        this.parent.repaint();
    }
}

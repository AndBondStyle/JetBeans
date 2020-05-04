package gui.propeditor.editors;

import javax.swing.*;

public class ObjectEditor extends EditorUI<Object> {
    private JLabel label;

    @Override
    protected void populateCenterPanel() {
        this.label = new JLabel();
        this.centerPanel.add(this.label);
    }

    @Override
    protected void update() {
        this.label.setText("" + this.value);
        this.parent.repaint();
    }
}

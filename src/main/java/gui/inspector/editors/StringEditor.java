package gui.inspector.editors;

import javax.swing.*;

public class StringEditor extends Editor {
    private JTextField text;

    @Override
    protected void populateCenterPanel() {
        this.text = new JTextField();
        this.text.setOpaque(false);
        this.text.setBorder(BorderFactory.createEmptyBorder());
        this.text.addActionListener((e) -> this.accept(this.text.getText(), true));
        this.centerPanel.add(this.text);
        if (!this.prop.isSettable()) this.text.setEnabled(false);
    }

    @Override
    protected void update() {
        this.text.setText((String) this.value);
        this.parent.repaint();
    }
}

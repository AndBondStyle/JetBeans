package gui.inspector.editors;

import com.intellij.ui.components.OnOffButton;

import javax.swing.*;

public class BooleanEditor extends Editor {
    private OnOffButton toggle;

    @Override
    protected void populateCenterPanel() {
        this.toggle = new OnOffButton();
        this.toggle.setOnText("TRUE");
        this.toggle.setOffText("FALSE");
        this.toggle.addChangeListener(e -> {
            boolean state = this.toggle.getModel().isSelected();
            if (state != (boolean) this.value) this.accept(state, true);
        });
        this.centerPanel.add(this.toggle);
        SwingUtilities.invokeLater(() -> {
            if (!this.prop.isSettable()) this.toggle.setEnabled(false);
        });
    }

    @Override
    protected void update() {
        this.toggle.setSelected((boolean) this.value);
        this.parent.repaint();
    }
}

package gui.inspector.editors;

import com.intellij.ui.JBIntSpinner;

import javax.swing.*;

public class IntegerEditor extends Editor {
    private JBIntSpinner spinner;

    @Override
    protected void populateCenterPanel() {
        this.spinner = new JBIntSpinner(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        this.spinner.setBorder(BorderFactory.createEmptyBorder());
        this.spinner.setOpaque(false);
        this.spinner.setPreferredSize(null);
        this.spinner.setMaximumSize(null);
        this.spinner.addChangeListener(e -> {
            this.accept(this.spinner.getModel().getValue(), true);
        });
        if (!this.prop.isSettable()) this.spinner.setEnabled(false);
        this.centerPanel.add(this.spinner);
        this.centerPanel.add(Box.createHorizontalGlue());
    }

    @Override
    protected void update() {
        this.spinner.getModel().setValue(this.value);
        this.parent.repaint();
    }
}

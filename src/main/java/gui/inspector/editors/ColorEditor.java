package gui.inspector.editors;

import com.intellij.ui.ColorPicker;
import com.intellij.ui.ShowColorPickerAction;
import com.intellij.ui.picker.ColorListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ColorEditor extends Editor {
    private JPanel colorDot;
    private JTextField text;

    @Override
    protected void populateCenterPanel() {
        this.colorDot = new JPanel();
        this.colorDot.setOpaque(true);
        this.colorDot.setBorder(BorderFactory.createEmptyBorder());
        this.colorDot.setPreferredSize(new Dimension(14, 14));
        this.colorDot.setMaximumSize(new Dimension(14, 14));
        this.text = new JTextField();
        this.text.setOpaque(false);
        this.text.setBorder(BorderFactory.createEmptyBorder());
        this.text.setEnabled(false);
        this.centerPanel.add(Box.createHorizontalStrut(6));
        this.centerPanel.add(this.colorDot);
        this.centerPanel.add(this.text);

        if (this.prop.isSettable()) {
            this.colorDot.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    ColorEditor.this.pick();
                }
            });
            this.text.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    ColorEditor.this.pick();
                }
            });
        }
    }

    private void pick() {
        ColorPicker.showColorPickerPopup(
                this.parent.project,
                (Color) this.value,
                (color, source) -> ColorEditor.this.accept(color, true)
        );
    }

    @Override
    protected void update() {
        Color color = (Color) this.value;
        this.colorDot.setBackground(color);
        if (color != null) {
            String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            this.text.setText(hex.toUpperCase());
        } else {
            this.text.setText("" + this.value);
        }
        this.parent.repaint();
    }
}

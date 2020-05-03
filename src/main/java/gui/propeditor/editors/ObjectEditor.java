package gui.propeditor.editors;

import gui.propeditor.tree.PropertyTree;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectEditor extends Editor<Object> {
    private JLabel label;

    public ObjectEditor(PropertyTree parent, Object target, String name, Class<?> type, Supplier<Object> getter, Consumer<Object> setter) {
        super(parent, target, name, type, getter, setter);
    }

    @Override
    protected void build() {
        this.label = new JLabel();
        this.add(this.label);
    }

    @Override
    protected void update() {
        this.label.setText("" + this.value);
        this.parent.repaint();
    }
}

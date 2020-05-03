package gui.propeditor.tree;

import gui.common.tree.PatchedNode;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class PropertyNodeEditor implements TreeCellEditor {
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if (value instanceof PropertyNode) return ((PropertyNode) value).getEditor();
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        if (!(event instanceof MouseEvent)) return false;
        if (!(event.getSource() instanceof JTree)) return false;
        MouseEvent e = (MouseEvent) event;
        PropertyTree tree = (PropertyTree) e.getSource();
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        PatchedNode node = (PatchedNode) path.getLastPathComponent();
        if (!(node instanceof PropertyNode)) return false;
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject event) {
        return true;
    }

    public Object getCellEditorValue() { return null; }
    public boolean stopCellEditing() { return true; }
    public void cancelCellEditing() {}
    public void addCellEditorListener(CellEditorListener l) {}
    public void removeCellEditorListener(CellEditorListener l) {}
}

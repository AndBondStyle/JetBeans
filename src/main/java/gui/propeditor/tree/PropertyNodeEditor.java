package gui.propeditor.tree;

import gui.common.tree.PatchedNode;

import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.*;
import java.awt.*;

public class PropertyNodeEditor implements TreeCellEditor {
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return ((PropertyNode) value).editor;
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        if (!(event instanceof MouseEvent)) return false;
        if (!(event.getSource() instanceof JTree)) return false;
        MouseEvent e = (MouseEvent) event;
        PropertyTree tree = (PropertyTree) e.getSource();
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        PatchedNode node = (PatchedNode) path.getLastPathComponent();
        return node instanceof PropertyNode;
    }

    public boolean shouldSelectCell(EventObject event) { return true; }
    public Object getCellEditorValue() { return null; }
    public boolean stopCellEditing() { return true; }
    public void cancelCellEditing() {}
    public void addCellEditorListener(CellEditorListener l) {}
    public void removeCellEditorListener(CellEditorListener l) {}
}

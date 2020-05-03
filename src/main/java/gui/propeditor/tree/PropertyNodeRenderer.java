package gui.propeditor.tree;

import com.intellij.ide.util.treeView.NodeRenderer;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.*;
import java.awt.*;

public class PropertyNodeRenderer implements TreeCellRenderer {
    private NodeRenderer defaultRenderer = new NodeRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof PropertyNode) return ((PropertyNode) value).getEditor();
        return this.defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}

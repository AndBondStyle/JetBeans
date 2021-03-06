package gui.inspector.tree;

import com.intellij.ide.util.treeView.NodeRenderer;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;
import java.awt.*;

public class PropertyNodeRenderer extends DefaultTreeCellRenderer {
    private NodeRenderer defaultRenderer = new NodeRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component defaultComponent = this.defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof PropertyNode) return ((PropertyNode) value).editor;
        return defaultComponent;
    }
}

package gui.common.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class PatchedTree extends SimpleTree {
    private Project project;

    public PatchedTree(Project project) {
        this.project = project;
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.setRoot(this.makeNode(""));
    }

    public PatchedNode makeNode(Object data) {
        return new PatchedNode(this.project, data);
    }

    public PatchedNode getRoot() {
        return (PatchedNode) this.getModel().getRoot();
    }

    public void forceUpdate() {
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        model.reload(root);
    }
}

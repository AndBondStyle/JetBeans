package gui.common.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.tree.*;
import java.util.Collections;
import java.util.HashMap;

public class PatchedTree extends SimpleTree {
    private HashMap<String, Boolean> expandedState = new HashMap<>();
    private Project project;

    public PatchedTree(Project project) {
        this.project = project;
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.setRoot(this.makeNode("__ROOT__"));
        this.expandRow(0);
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public PatchedNode makeNode(Object data) {
        return new PatchedNode(this.project, data);
    }

    public PatchedNode getRoot() {
        return (PatchedNode) this.getModel().getRoot();
    }

    public void forceUpdate() {
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.reload(root);
    }

    public void saveExpandedState() {
        this.expandedState.clear();
        saveNodeState(this.getRoot());
        this.expandedState.put(this.getRoot().getData(), false);
    }

    private void saveNodeState(PatchedNode node) {
        TreePath path = TreeUtil.getPathFromRoot(node);
        this.expandedState.put(node.getData(), this.isExpanded(path));
        for (TreeNode child : Collections.list(node.children())) saveNodeState((PatchedNode) child);
    }

    public void restoreExpandedState() {
        restoreNodeState(this.getRoot());
    }

    private void restoreNodeState(PatchedNode node) {
        TreePath path = TreeUtil.getPathFromRoot(node);
        if (this.expandedState.getOrDefault(node.getData(), false)) this.expandPath(path);
        for (TreeNode child : Collections.list(node.children())) restoreNodeState((PatchedNode) child);
    }
}

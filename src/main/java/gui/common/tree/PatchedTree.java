package gui.common.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.tree.*;
import java.util.*;

public class PatchedTree extends SimpleTree {
    private HashMap<String, Boolean> expandedState = new HashMap<>();
    private Project project;

    public PatchedTree(Project project) {
        this.project = project;
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.setRoot(new PatchedNode(this.project, "__ROOT__"));
        this.expandRow(0);
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
        this.expandedState.put(this.getRoot().getKey(), false);
    }

    private void saveNodeState(PatchedNode node) {
        TreePath path = TreeUtil.getPathFromRoot(node);
        this.expandedState.put(node.getKey(), this.isExpanded(path));
        for (TreeNode child : Collections.list(node.children())) saveNodeState((PatchedNode) child);
    }

    public void restoreExpandedState() {
        restoreNodeState(this.getRoot());
    }

    private void restoreNodeState(PatchedNode node) {
        TreePath path = TreeUtil.getPathFromRoot(node);
        if (this.expandedState.getOrDefault(node.getKey(), false)) this.expandPath(path);
        for (TreeNode child : Collections.list(node.children())) restoreNodeState((PatchedNode) child);
    }

    public void autoSort(PatchedNode node) {
        if (node == null) node = this.getRoot();
        List<PatchedNode> children = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) children.add((PatchedNode) node.getChildAt(i));
        children.sort(Comparator.comparing(PatchedNode::getPrimaryText));
        node.removeAllChildren();
        children.forEach(node::add);
    }
}

package gui.library;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.treeStructure.Tree;
import gui.library.actions.TestAction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ClassesPanel extends SimpleToolWindowPanel {
    public LibraryView parent;

    public ClassesPanel(LibraryView parent) {
        super(false, true);
        this.parent = parent;
        this.initToolbar();
        this.initContent();
    }

    void initToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new TestAction());
        ActionToolbar toolbar = ActionManager.getInstance()
                .createActionToolbar("JetBeansLibraryToolbar", actionGroup, false);
        this.setToolbar(toolbar.getComponent());
    }

    void initContent() {
        DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
        Tree tree = new Tree(model);
        this.setContent(tree);
    }
}

package gui.library;

import gui.library.actions.TestAction;
import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import core.registry.loaders.ClassLoaderBase;
import core.JetBeans;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;

import javax.swing.tree.*;
import java.util.Map;

public class ClassesPanel extends SimpleToolWindowPanel {
    public LibraryView parent;
    public PatchedTree tree;
    public JetBeans core;

    public ClassesPanel(LibraryView parent, Project project) {
        super(false, true);
        this.parent = parent;
        this.core = JetBeans.getInstance(project);
        this.core.getRegistry().addListener(e -> {
            if (e.getActionCommand().equals("update")) this.update();
        });
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
        this.tree = new PatchedTree(this.core.getProject());
        this.tree.expandRow(0);
        this.tree.setRootVisible(false);
        this.tree.setShowsRootHandles(true);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setContent(ScrollPaneFactory.createScrollPane(this.tree));
        this.update();
    }

    public void update() {
        PatchedNode root = this.tree.getRoot();
        root.removeAllChildren();
        for (Map.Entry<String, ClassLoaderBase> loader : this.core.getRegistry().getLoaders().entrySet()) {
            PatchedNode groupNode = this.tree.makeNode("");
            groupNode.setPrimaryText(loader.getKey());
            groupNode.setSecondaryText(loader.getValue().getSecondaryText());
            groupNode.setIcon(loader.getValue().getIcon());
            for (Map.Entry<String, String> klass : loader.getValue().getClasses().entrySet()) {
                PatchedNode childNode = this.tree.makeNode(klass.getValue());
                childNode.setPrimaryText(klass.getKey());
                childNode.setIcon(AllIcons.Nodes.Class);
                groupNode.add(childNode);
            }
            root.add(groupNode);
        }
        this.tree.forceUpdate();
    }
}

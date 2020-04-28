package gui.library;

import gui.library.actions.TestAction;
import com.intellij.icons.AllIcons;
import gui.common.PatchedNode;
import gui.common.PatchedTree;
import core.JetBeans;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.*;

public class ClassesPanel extends SimpleToolWindowPanel {
    public LibraryView parent;
    public PatchedTree tree;
    public JetBeans core;

    public ClassesPanel(LibraryView parent, Project project) {
        super(false, true);
        this.parent = parent;
        this.core = JetBeans.getInstance(project);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("registry")) this.update();
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
        List<List<String>> data = new ArrayList<>();
        List<String> a = new ArrayList<>();
        a.add("Group 1");
        a.add("Item a");
        a.add("Item b");
        a.add("Item c");
        data.add(a);
        List<String> b = new ArrayList<>();
        b.add("Group 2");
        b.add("Item a");
        b.add("Item b");
        b.add("Item c");
        data.add(b);

        PatchedNode root = this.tree.getRoot();
        for (List<String> group : data) {
            PatchedNode groupNode = this.tree.makeNode(group.get(0));
            groupNode.setPrimaryText(group.get(0));
            groupNode.setIcon(AllIcons.Nodes.NativeLibrariesFolder);
            for (String item : group) {
                PatchedNode childNode = this.tree.makeNode(item);
                childNode.setPrimaryText(item);
                childNode.setSecondaryText("secondary");
                childNode.setIcon(AllIcons.Nodes.Class);
                groupNode.add(childNode);
            }
            root.add(groupNode);
        }
        this.tree.forceUpdate();
    }
}

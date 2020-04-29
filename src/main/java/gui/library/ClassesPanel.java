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
import gui.wrapper.Wrapper;

import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
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
        this.tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                if (e.getClickCount() != 2) return;
                PatchedNode node = (PatchedNode) tree.getLastSelectedPathComponent();
                if (node.getData().startsWith("!")) return;
                Component component = (Component) core.getRegistry().instantiate(node.getData());
                // TODO: Autowrap helper function
                Wrapper wrapper = new Wrapper(component);
                // TODO: Activate placing action
                core.getCanvas().addItem(wrapper);
                wrapper.setLocation(100, 100);
                core.getCanvas().setSelection(wrapper);
            }
        });
        this.setContent(ScrollPaneFactory.createScrollPane(this.tree));
        this.update();
    }

    public void update() {
        PatchedNode root = this.tree.getRoot();
        this.tree.saveExpandedState();
        root.removeAllChildren();
        for (Map.Entry<String, ClassLoaderBase> loader : this.core.getRegistry().getLoaders().entrySet()) {
            PatchedNode groupNode = this.tree.makeNode("!" + loader.getKey());
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
        this.tree.restoreExpandedState();
    }
}

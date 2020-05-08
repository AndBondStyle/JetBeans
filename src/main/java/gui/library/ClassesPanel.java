package gui.library;

import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import core.registry.loaders.Loader;
import core.JetBeans;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class ClassesPanel extends SimpleToolWindowPanel {
    public PatchedTree tree;
    public JetBeans core;

    public ClassesPanel(Project project) {
        super(false, true);
        this.core = JetBeans.getInstance(project);
        this.core.registry.addListener(e -> {
            if (e.getActionCommand().equals("update")) this.update();
        });
        this.initContent();
    }

    void initContent() {
        this.tree = new PatchedTree(this.core.project);
        this.tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2) return;
                PatchedNode node = (PatchedNode) tree.getLastSelectedPathComponent();
                if (node == null || node.getKey().startsWith("!")) return;
                core.instantiate(node.getKey());
            }
        });
        this.setContent(ScrollPaneFactory.createScrollPane(this.tree));
        this.update();
    }

    public void update() {
        PatchedNode root = this.tree.getRoot();
        this.tree.saveExpandedState();
        root.removeAllChildren();
        for (Map.Entry<String, Loader> loader : this.core.registry.getLoaders().entrySet()) {
            PatchedNode groupNode = new PatchedNode(this.core.project, "!" + loader.getKey());
            groupNode.setPrimaryText(loader.getValue().getPrimaryText());
            groupNode.setSecondaryText(loader.getValue().getSecondaryText());
            groupNode.setIcon(loader.getValue().getIcon());
            for (Map.Entry<String, String> klass : loader.getValue().getClasses().entrySet()) {
                List<String> tokens = new ArrayList<>(Arrays.asList(klass.getKey().split("\\.")));
                String name = tokens.remove(tokens.size() - 1);
                String pkg = String.join(".", tokens);
                PatchedNode childNode = new PatchedNode(this.core.project, klass.getValue());
                childNode.setPrimaryText(name);
                if (!pkg.equals("")) childNode.setSecondaryText(pkg);
                childNode.setIcon(AllIcons.Nodes.Class);
                groupNode.add(childNode);
            }
            root.add(groupNode);
        }
        this.tree.forceUpdate();
        this.tree.restoreExpandedState();
    }
}

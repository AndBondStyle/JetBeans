package gui.library;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.util.Pair;
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
        this.core.loader.addListener(e -> {
            if (e.getActionCommand().equals("updated")) this.update();
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
        for (Map.Entry<PresentationData, List<Pair<String, PresentationData>>> group : this.core.loader.groups.entrySet()) {
            PatchedNode groupNode = new PatchedNode(this.core.project, "!" + group.getKey().getPresentableText());
            groupNode.presentation.copyFrom(group.getKey());
            for (Pair<String, PresentationData> item : group.getValue()) {
                PatchedNode childNode = new PatchedNode(this.core.project, item.getFirst());
                childNode.presentation.copyFrom(item.getSecond());
                groupNode.add(childNode);
            }
            root.add(groupNode);
        }
        this.tree.forceUpdate();
        this.tree.restoreExpandedState();
    }
}

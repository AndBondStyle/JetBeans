package gui.library;

import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import core.JetBeans;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;

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
                String klass = LibraryView.getClassName(ClassesPanel.this.core.project);
                if (klass == null) return;
                ClassesPanel.this.core.instantiate(klass, null, true);
            }
        });
        this.setContent(ScrollPaneFactory.createScrollPane(this.tree));
        this.update();
    }

    public void update() {
        PatchedNode root = this.tree.getRoot();
        this.tree.saveExpandedState();
        root.removeAllChildren();
        this.core.loader.groups.forEach(root::add);
        this.tree.forceUpdate();
        this.tree.restoreExpandedState();
    }
}

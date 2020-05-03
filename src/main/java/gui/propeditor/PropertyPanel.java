package gui.propeditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;
import gui.propeditor.tree.PropertyTree;
import gui.propeditor.tree.PropertyTreeUI;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class PropertyPanel extends SimpleToolWindowPanel {
    public PropertyTree tree;
    public Project project;

    public PropertyPanel(Project project) {
        super(false, true);
        this.project = project;
        this.initToolbar();
        this.initContent();
    }

    void initToolbar() {
        // TODO
    }

    void initContent() {
        this.tree = new PropertyTree(this.project);
        JScrollPane scroll = ScrollPaneFactory.createScrollPane(this.tree);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.tree.setUI(new PropertyTreeUI(scroll));
        this.setContent(scroll);
    }
}

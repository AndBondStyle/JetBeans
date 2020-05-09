package gui.inspector;

import gui.inspector.tree.PropertyTree;
import gui.wrapper.Wrapper;
import core.main.JetBeans;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;

import javax.swing.*;

public class PropertyPanel extends SimpleToolWindowPanel {
    public PropertyTree tree;
    public JetBeans core;

    public PropertyPanel(Project project) {
        super(false, true);
        this.core = JetBeans.getInstance(project);
        this.initContent();
    }

    void initContent() {
        this.tree = new PropertyTree(this.core.project);
        JScrollPane scroll = ScrollPaneFactory.createScrollPane(this.tree);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setContent(scroll);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("select")) {
                if (!(this.core.selection instanceof Wrapper)) this.tree.setTarget(null);
                else this.tree.setTarget(((Wrapper) this.core.selection).getTarget());
            }
        });
    }
}

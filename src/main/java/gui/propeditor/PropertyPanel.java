package gui.propeditor;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;
import core.JetBeans;
import gui.canvas.CanvasItem;
import gui.library.actions.LoadJarAction;
import gui.propeditor.actions.BindAction;
import gui.propeditor.actions.CollapseAllAction;
import gui.propeditor.actions.ShellInputAction;
import gui.propeditor.tree.PropertyTree;
import gui.propeditor.tree.PropertyTreeUI;
import gui.wrapper.Wrapper;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class PropertyPanel extends SimpleToolWindowPanel {
    public PropertyTree tree;
    public JetBeans core;

    public PropertyPanel(Project project) {
        super(false, true);
        this.core = JetBeans.getInstance(project);
        this.initContent();
        this.initToolbar();
    }

    void initToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new BindAction(this.tree));
        actionGroup.add(new CollapseAllAction(this.tree));
        ActionToolbar toolbar = ActionManager.getInstance()
                .createActionToolbar("JetBeansPropertyToolbar", actionGroup, false);
        this.setToolbar(toolbar.getComponent());
    }

    void initContent() {
        this.tree = new PropertyTree(this.core.getProject());
        JScrollPane scroll = ScrollPaneFactory.createScrollPane(this.tree);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.tree.setUI(new PropertyTreeUI(scroll));
        this.setContent(scroll);

        this.core.addListener(e -> {
            if (e.getActionCommand().equals("select")) {
                CanvasItem selection = this.core.getSelection();
                if (selection instanceof Wrapper) {
                    Object target = ((Wrapper) selection).getTarget();
                    this.tree.setTarget(target);
                } else {
                    this.tree.setTarget(null);
                }
            }
        });
    }
}

package gui.propeditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;
import gui.propeditor.tree.PropertyTree;

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
        this.tree.expandRow(0);
        this.tree.setRootVisible(false);
        this.tree.setShowsRootHandles(true);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//        this.tree.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent e) {
//                // TODO?
//            }
//        });
        this.setContent(ScrollPaneFactory.createScrollPane(this.tree));
    }
}

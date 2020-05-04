package gui.propeditor.tree;

import core.JetBeans;
import gui.link.Link;
import gui.canvas.CanvasItem;
import gui.propeditor.editors.EditorUI;
import gui.wrapper.Wrapper;
import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import gui.propeditor.editors.Editor;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.List;

public class PropertyTree extends PatchedTree {
    private List<Editor<?>> editors = new ArrayList<>();
    private JetBeans core;

    public PropertyTree(Project project) {
        super(project);
        this.setCellRenderer(new PropertyNodeRenderer());
        this.setCellEditor(new PropertyNodeEditor());
        this.setEditable(true);
        this.expandRow(0);
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        this.core = JetBeans.getInstance(project);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("select")) {
                CanvasItem selection = this.core.getSelection();
                if (selection == null) this.setTarget(null);
                if (selection instanceof Wrapper) {
                    Object target = ((Wrapper) selection).getTarget();
                    this.setTarget(target);
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                PropertyTree.this.forceUpdate();
            }
        });
    }

    public void updateSplitters(float proportion) {
        for (Editor<?> editor : this.editors) ((EditorUI<?>) editor).updateSplitter(proportion);
        this.repaint();
    }

    public void setTarget(Object target) {
        // Unbind listeners & reset
        this.editors.forEach(Editor::dispose);
        this.editors.clear();
        PatchedNode root = this.getRoot();
        root.removeAllChildren();
        this.forceUpdate();
        if (target == null) return;

        // Analyze target bean
        PropertyDescriptor[] props;
        try {
            BeanInfo bi = Introspector.getBeanInfo(target.getClass());
            props = bi.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            String message = "Failed to analyze bean \"" + target + "\"";
            throw new RuntimeException(message, e);
        }

        // Build property tree
        // TODO: Property grouping & sorting
        for (PropertyDescriptor prop : props) {
            Editor<?> editor = Editor.createEditor(this, prop, target);
            if (editor == null) continue;
            PropertyNode node = new PropertyNode(editor, this.core.getProject());
            this.editors.add(editor);
            root.add(node);
        }
        this.editors.forEach(Editor::init);
        this.forceUpdate();
    }
}

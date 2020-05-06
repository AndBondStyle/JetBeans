package gui.propeditor.tree;

import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import gui.propeditor.editors.Editor;

import com.intellij.openapi.project.Project;
import com.intellij.icons.AllIcons;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import java.lang.reflect.Method;
import java.util.List;
import java.beans.*;
import java.util.*;

public class PropertyTree extends PatchedTree {
    public PropertyTreeSettings settings = new PropertyTreeSettings();
    private List<Editor> editors = new ArrayList<>();
    private Project project;

    public PropertyTree(Project project) {
        super(project);
        this.project = project;
        this.setEditable(true);
        this.setCellRenderer(new PropertyNodeRenderer());
        this.setCellEditor(new PropertyNodeEditor());
        this.getExpandableItemsHandler().setEnabled(false);
        this.settings.addListener((e) -> this.rebuild());
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) { PropertyTree.this.forceResize(); }
        });
        this.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) { PropertyTree.this.forceResize(); }
        });
    }

    public void forceResize() {
        Editor editor = PropertyTree.this.getActiveEditor();
        if (editor != null) {
            try {
                // https://stackoverflow.com/questions/22330502/
                Method method = BasicTreeUI.class.getDeclaredMethod("configureLayoutCache");
                method.setAccessible(true);
                method.invoke(PropertyTree.this.getUI());
            } catch (ReflectiveOperationException ignored) {}
        }
    }

    public void updateSplitters(float proportion) {
        for (Editor editor : this.editors) editor.updateSplitter(proportion);
        this.repaint();
    }

    public Editor getActiveEditor() {
        TreePath path = this.getSelectionPath();
        if (path == null) return null;
        PatchedNode node = (PatchedNode) path.getLastPathComponent();
        if (!(node instanceof PropertyNode)) return null;
        return ((PropertyNode) node).editor;
    }

    public void setTarget(Object target) {
        this.editors.forEach(Editor::dispose);
        this.editors.clear();
        this.rebuild();
        if (target == null) return;

        PropertyDescriptor[] props;
        try {
            BeanInfo bi = Introspector.getBeanInfo(target.getClass());
            props = bi.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            String message = "Failed to analyze bean \"" + target + "\"";
            throw new RuntimeException(message, e);
        }

        for (PropertyDescriptor prop : props) {
            Editor editor = Editor.createEditor(this, prop, target);
            if (editor == null) continue;
            this.editors.add(editor);
        }
        this.editors.forEach(Editor::init);
        this.rebuild();
    }

    public void rebuild() {
        PatchedNode root = this.getRoot();
        PropertyNode[] nodes = this.editors.stream()
                .map(editor -> new PropertyNode(editor, this.project))
                .toArray(PropertyNode[]::new);
        nodes = this.settings.filterNodes(nodes);
        nodes = this.settings.sortNodes(nodes);
        HashMap<String, List<PropertyNode>> groups = this.settings.groupNodes(nodes);
        if (groups == null) {
            root.removeAllChildren();
            Arrays.asList(nodes).forEach(root::add);
        } else {
            this.saveExpandedState();
            root.removeAllChildren();
            for (Map.Entry<String, List<PropertyNode>> group : groups.entrySet()) {
                String[] tokens = group.getKey().split("!");
                PatchedNode groupNode = new PatchedNode(this.project, "!" + tokens[0]);
                groupNode.setPrimaryText(tokens[0]);
                if (tokens.length > 1) groupNode.setSecondaryText(tokens[1]);
                groupNode.setIcon(AllIcons.Nodes.Class);
                for (PropertyNode node : group.getValue()) groupNode.add(node);
                root.add(groupNode);
            }
        }
        this.forceUpdate();
        this.restoreExpandedState();
    }
}

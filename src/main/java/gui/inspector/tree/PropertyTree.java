package gui.inspector.tree;

import core.inspection.InstanceInfo;
import core.inspection.PropertyInfo;
import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import gui.inspector.InspectorView;
import gui.inspector.TreeSettings;
import gui.inspector.editors.Editor;

import com.intellij.openapi.project.Project;
import com.intellij.icons.AllIcons;
import gui.wrapper.Wrapper;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

public class PropertyTree extends PatchedTree {
    private List<Editor> editors = new ArrayList<>();
    private TreeSettings settings;
    private Project project;

    public PropertyTree(Project project) {
        super(project);
        this.settings = project.getService(InspectorView.class).settings;
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
        for (PropertyInfo prop : InstanceInfo.fetch(target).props) {
            Editor editor = Editor.createEditor(this, prop);
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
        HashMap<String, List<PatchedNode>> groups = this.settings.groupPropertyNodes(nodes);
        if (groups == null) {
            root.removeAllChildren();
            Arrays.asList(nodes).forEach(root::add);
        } else {
            this.saveExpandedState();
            root.removeAllChildren();
            PatchedNode lastNode = null;
            for (Map.Entry<String, List<PatchedNode>> group : groups.entrySet()) {
                PatchedNode groupNode = new PatchedNode(this.project, group.getKey());
                groupNode.setPrimaryText(group.getKey());
                groupNode.setSecondaryText("inherited");
                groupNode.setIcon(AllIcons.Nodes.Class);
                for (PatchedNode node : group.getValue()) groupNode.add(node);
                root.add(groupNode);
                lastNode = groupNode;
            }
            if (lastNode != null) lastNode.setSecondaryText("own properties");
        }
        this.forceUpdate();
        this.restoreExpandedState();
    }
}

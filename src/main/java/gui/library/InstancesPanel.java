package gui.library;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.tree.TreeUtil;
import core.JetBeans;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import gui.canvas.Canvas;
import gui.canvas.CanvasItem;
import gui.common.tree.PatchedNode;
import gui.common.tree.PatchedTree;
import gui.link.Link;
import gui.wrapper.Wrapper;

import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class InstancesPanel extends SimpleToolWindowPanel {
    private HashMap<CanvasItem, PatchedNode> mapping = new HashMap<>();
    public PatchedTree tree;
    public JetBeans core;

    public InstancesPanel(Project project) {
        super(false, true);
        this.core = JetBeans.getInstance(project);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("instantiate")) this.rebuild();
            if (e.getActionCommand().equals("select")) this.reselect();
        });
        this.initContent();
    }

    private void initContent() {
        this.tree = new PatchedTree(this.core.getProject());
        this.tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2) return;
                PatchedNode node = (PatchedNode) tree.getLastSelectedPathComponent();
                if (node == null || !(node.getRawData() instanceof CanvasItem)) return;
                Canvas canvas = core.getCanvas();
                if (canvas != null) {
                    CanvasItem item = (CanvasItem) node.getRawData();
                    canvas.setSelection(item);
                    canvas.scrollToItem(item);
                }
            }
        });
        this.setContent(ScrollPaneFactory.createScrollPane(this.tree));
        this.rebuild();
    }

    private void rebuild() {
        PatchedNode root = this.tree.getRoot();
        root.removeAllChildren();
        this.mapping.clear();
        Canvas canvas = this.core.getCanvas();
        if (canvas == null) return;
        for (CanvasItem item : canvas.items) {
            if (!(item instanceof Wrapper)) continue;
            PatchedNode node = new PatchedNode(this.core.getProject(), item);
            node.setPrimaryText(((Wrapper) item).getTarget().getClass().getSimpleName());
            node.setSecondaryText(item.toString());
            node.setIcon(AllIcons.Nodes.Class);
            root.add(node);
            this.mapping.put(item, node);
        }
        this.tree.forceUpdate();
    }

    private void reselect() {
        CanvasItem selection = this.core.getSelection();
        PatchedNode node = this.mapping.get(selection);
        this.tree.getSelectionModel().clearSelection();
        if (!(selection instanceof Wrapper) || node == null) return;
        TreePath path = TreeUtil.getPathFromRoot(node);
        this.tree.getSelectionModel().setSelectionPath(path);
    }
}

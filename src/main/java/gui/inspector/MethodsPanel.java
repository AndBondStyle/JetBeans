package gui.inspector;

import core.inspection.InstanceInfo;
import core.inspection.MethodInfo;
import gui.common.tree.PatchedTree;
import gui.common.tree.PatchedNode;
import gui.wrapper.Wrapper;
import core.JetBeans;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class MethodsPanel extends SimpleToolWindowPanel {
    public TreeSettings settings;
    public PatchedTree tree;
    public JetBeans core;

    public MethodsPanel(Project project) {
        super(false, true);
        this.core = JetBeans.getInstance(project);
        this.tree = new PatchedTree(this.core.project);
        this.settings = project.getService(InspectorView.class).settings;
        this.core.addListener(e -> { if (e.getActionCommand().equals("select")) this.update(); });
        this.settings.addListener(e -> { if (e.getActionCommand().equals("update")) this.update(); });
        JScrollPane scroll = ScrollPaneFactory.createScrollPane(this.tree);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setContent(scroll);
    }

    public void update() {
        PatchedNode root = this.tree.getRoot();
        if (!(this.core.selection instanceof Wrapper)) {
            root.removeAllChildren();
            return;
        }
        Object target = ((Wrapper) this.core.selection).getTarget();
        InstanceInfo info = InstanceInfo.fetch(target);

        HashMap<PatchedNode, Object> nodes = new LinkedHashMap<>();
        List<MethodInfo> methods = info.methods.stream()
                .sorted(Comparator.comparing(x -> x.descriptor.getDisplayName()))
                .collect(Collectors.toList());
        for (MethodInfo method : methods) {
            String key = method.descriptor.getDisplayName() + " " + method.getSignature();
            PatchedNode node = new PatchedNode(this.core.project, key, method);
            node.setPrimaryText(method.descriptor.getDisplayName());
            node.setSecondaryText(method.getSignature());
            node.setIcon(AllIcons.Nodes.Method);
            nodes.put(node, method);
        }
        HashMap<String, List<PatchedNode>> groups = this.settings.groupNodes(nodes);
        if (groups == null) {
            root.removeAllChildren();
            List<PatchedNode> items = this.groupOverloads(new ArrayList<>(nodes.keySet()));
            items.forEach(root::add);
        } else {
            this.tree.saveExpandedState();
            root.removeAllChildren();
            PatchedNode lastNode = null;
            for (Map.Entry<String, List<PatchedNode>> group : groups.entrySet()) {
                PatchedNode groupNode = new PatchedNode(this.core.project, group.getKey());
                groupNode.setSecondaryText("inherited");
                groupNode.setIcon(AllIcons.Nodes.Class);
                List<PatchedNode> items = this.groupOverloads(group.getValue());
                for (PatchedNode node : items) groupNode.add(node);
                root.add(groupNode);
                lastNode = groupNode;
            }
            if (lastNode != null) lastNode.setSecondaryText("own methods");
        }

        this.tree.forceUpdate();
        this.tree.restoreExpandedState();
    }

    public List<PatchedNode> groupOverloads(List<PatchedNode> nodes) {
        HashMap<String, PatchedNode> cache = new LinkedHashMap<>();
        for (PatchedNode node : nodes) {
            MethodInfo method = (MethodInfo) node.getValue();
            String name = method.descriptor.getDisplayName();
            PatchedNode overloads = cache.get(name);
            if (overloads == null) {
                overloads = new PatchedNode(this.core.project, name + " overloads");
                overloads.setPrimaryText(name);
                overloads.setIcon(AllIcons.Debugger.MultipleBreakpoints);
                cache.put(name, overloads);
            }
            overloads.add(node);
        }
        List<PatchedNode> result = new ArrayList<>();
        for (PatchedNode overloads : cache.values()) {
            if (overloads.getChildCount() == 1) {
                result.add((PatchedNode) overloads.getChildAt(0));
            } else {
                overloads.setSecondaryText(overloads.getChildCount() + " overloads");
                result.add(overloads);
            }
        }
        return result;
    }
}

package gui.inspector;

import core.inspection.EventInfo;
import core.inspection.EventSetInfo;
import core.inspection.InstanceInfo;
import gui.common.tree.PatchedTree;
import gui.common.tree.PatchedNode;
import gui.canvas.CanvasItem;
import gui.wrapper.Wrapper;
import core.JetBeans;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.beans.*;

public class EventsPanel extends SimpleToolWindowPanel {
    public PatchedTree tree;
    public JetBeans core;

    public EventsPanel(Project project) {
        super(false, true);
        this.core = JetBeans.getInstance(project);
        this.tree = new PatchedTree(this.core.project);
        this.core.addListener(e -> { if (e.getActionCommand().equals("select")) this.update(); });
        JScrollPane scroll = ScrollPaneFactory.createScrollPane(this.tree);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setContent(scroll);
    }

    public void update() {
        PatchedNode root = this.tree.getRoot();
        if (!(this.core.selection instanceof Wrapper)) {
            root.removeAllChildren();
            this.tree.forceUpdate();
            return;
        }
        this.tree.saveExpandedState();
        root.removeAllChildren();
        Object target = ((Wrapper) this.core.selection).getTarget();
        InstanceInfo info = InstanceInfo.fetch(target);

        for (EventSetInfo eventSet : info.events) {
            PatchedNode group = new PatchedNode(this.core.project, eventSet.descriptor.getName());
            group.setPrimaryText(eventSet.descriptor.getDisplayName());
            group.setIcon(AllIcons.Nodes.ModuleGroup);
            group.setSecondaryText("Listeners: " + eventSet.getListenersCount());

            for (EventInfo event : eventSet.events) {
                PatchedNode node = new PatchedNode(this.core.project, event.method.getName(), event);
                node.setPrimaryText(event.method.getName());
                node.setIcon(AllIcons.Nodes.Enum);
                group.add(node);
            }
            root.add(group);
        }

        this.tree.forceUpdate();
        this.tree.restoreExpandedState();
    }
}

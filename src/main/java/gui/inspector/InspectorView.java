package gui.inspector;

import core.inspection.EventInfo;
import core.inspection.MethodInfo;
import core.inspection.PropertyInfo;
import gui.common.tree.PatchedNode;
import gui.inspector.actions.ShellExecuteAction;
import gui.inspector.actions.ViewSettingsAction;
import gui.inspector.actions.LinkAction;
import gui.common.CollapseAllAction;
import core.JetBeans;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.content.ContentManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;

@Service
public final class InspectorView implements ToolWindowFactory, DumbAware {
    public static String TOOLBAR_KEY = "JetBeans:InspectorToolbar";
    public static String PROPERTIES_TAB = "Properties";
    public static String EVENTS_TAB = "Events";
    public static String METHODS_TAB = "Methods";

    public JetBeans core;
    public ToolWindow toolWindow;
    public TreeSettings settings;

    public Content propsContent;
    public Content eventsContent;
    public Content methodsContent;
    public PropertyPanel props;
    public EventsPanel events;
    public MethodsPanel methods;

    public InspectorView() {}

    public InspectorView(Project project) {
        this.core = JetBeans.getInstance(project);
        this.settings = new TreeSettings();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        project.getService(InspectorView.class).initToolWindow(toolWindow);
    }

    public void initToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.props = new PropertyPanel(this.core.project);
        this.events = new EventsPanel(this.core.project);
        this.methods = new MethodsPanel(this.core.project);
        this.props.setToolbar(this.initToolbar().getComponent());
        this.events.setToolbar(this.initToolbar().getComponent());
        this.methods.setToolbar(this.initToolbar().getComponent());

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        this.propsContent = contentFactory.createContent(this.props, PROPERTIES_TAB, false);
        this.eventsContent = contentFactory.createContent(this.events, EVENTS_TAB, false);
        this.methodsContent = contentFactory.createContent(this.methods, METHODS_TAB, false);
        toolWindow.getContentManager().addContent(this.propsContent);
        toolWindow.getContentManager().addContent(this.eventsContent);
        toolWindow.getContentManager().addContent(this.methodsContent);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("activate")) toolWindow.show(null);
            if (e.getActionCommand().equals("deactivate")) toolWindow.hide(null);
        });
    }

    public ActionToolbar initToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new LinkAction());
        actionGroup.add(new ShellExecuteAction());
        actionGroup.add(new ViewSettingsAction(this.settings));
        actionGroup.add(new CollapseAllAction(this.getActiveTree()));
        return ActionManager.getInstance().createActionToolbar(TOOLBAR_KEY, actionGroup, false);
    }

    public String getActiveTab() {
        Content content = this.toolWindow.getContentManager().getSelectedContent();
        if (content == null) return null;
        return content.getTabName();
    }

    public JTree getActiveTree() {
        String tab = this.getActiveTab();
        if (tab == null) return null;
        if (tab.equals(PROPERTIES_TAB)) return this.props.tree;
        if (tab.equals(EVENTS_TAB)) return this.events.tree;
        if (tab.equals(METHODS_TAB)) return this.methods.tree;
        return null;
    }

    public Object getActiveItem() {
        JTree tree = this.getActiveTree();
        if (tree == null) return null;
        TreePath path = tree.getSelectionPath();
        if (path == null) return null;
        PatchedNode node = (PatchedNode) path.getLastPathComponent();
        Object item = node.getValue();
        if (item instanceof PropertyInfo || item instanceof EventInfo || item instanceof MethodInfo) return item;
        return null;
    }
}

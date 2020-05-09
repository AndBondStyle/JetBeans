package gui.library;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.content.ContentManager;
import core.main.JetBeans;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.Content;
import core.actions.CollapseAllAction;
import gui.common.tree.PatchedNode;
import core.actions.DeleteAction;
import core.actions.InstantiateAction;
import core.actions.ImportAction;
import core.actions.ReloadAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

@Service
public final class LibraryView implements ToolWindowFactory, DumbAware {
    public static String TOOLBAR_KEY = "JetBeans:LibraryToolbar";
    public static String CLASSES_TAB = "Classes";
    public static String INSTANCES_TAB = "Instances";

    public JetBeans core;
    public ToolWindow toolWindow;

    public Content classesContent;
    public Content instancesContent;
    public ClassesPanel classes;
    public InstancesPanel instances;

    public LibraryView() {}

    public LibraryView(Project project) {
        this.core = JetBeans.getInstance(project);
        this.classes = new ClassesPanel(project);
        this.instances = new InstancesPanel(project);
        this.classes.setToolbar(this.initToolbar().getComponent());
        this.instances.setToolbar(this.initToolbar().getComponent());
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        LibraryView view = project.getService(LibraryView.class);
        view.initToolWindow(toolWindow);
        view.toolWindow = toolWindow;
    }

    public void initToolWindow(ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        this.classesContent = contentFactory.createContent(this.classes, CLASSES_TAB, false);
        this.instancesContent = contentFactory.createContent(this.instances, INSTANCES_TAB, false);
        toolWindow.getContentManager().addContent(this.classesContent);
        toolWindow.getContentManager().addContent(this.instancesContent);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("activate")) toolWindow.show(null);
            if (e.getActionCommand().equals("deactivate")) toolWindow.hide(null);
        });
    }

    public ActionToolbar initToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new InstantiateAction());
        actionGroup.add(new ImportAction());
        actionGroup.add(new ReloadAction());
        actionGroup.add(new DeleteAction());
        actionGroup.add(new CollapseAllAction(() -> {
            String tab = this.getActiveTab();
            if (tab == null) return null;
            if (tab.equals(CLASSES_TAB)) return this.classes.tree;
            if (tab.equals(INSTANCES_TAB)) return this.instances.tree;
            return null;
        }));
        return ActionManager.getInstance().createActionToolbar(TOOLBAR_KEY, actionGroup, false);
    }

    public static LibraryView getInstance(Project project) {
        return project.getService(LibraryView.class);
    }

    public String getActiveTab() {
        Content content = this.toolWindow.getContentManager().getSelectedContent();
        if (content == null) return "";
        return content.getTabName();
    }

    public void setActiveTab(String tab) {
        ContentManager manager = this.toolWindow.getContentManager();
        if (tab.equals(CLASSES_TAB)) manager.setSelectedContent(this.classesContent);
        if (tab.equals(INSTANCES_TAB)) manager.setSelectedContent(this.instancesContent);
    }

    public static PatchedNode getActiveNode(Project project) {
        LibraryView view = project.getService(LibraryView.class);
        if (!view.getActiveTab().equals(LibraryView.CLASSES_TAB)) return null;
        TreePath path = view.classes.tree.getSelectionPath();
        if (path == null) return null;
        return (PatchedNode) path.getLastPathComponent();
    }

    public static String getClassName(Project project) {
        PatchedNode node = LibraryView.getActiveNode(project);
        if (node == null) return null;
        if (node.getKey().startsWith("!")) return null;
        if (node.getKey().contains("/")) return null;
        return node.getKey();
    }
}

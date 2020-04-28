package gui.library;

import gui.library.classes.ClassesPanel;
import core.JetBeans;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

@Service
public final class LibraryView implements ToolWindowFactory, DumbAware {
    public JetBeans core;
    public ClassesPanel classes;
    public InstancesPanel instances;

    public LibraryView() {}

    public LibraryView(Project project) {
        this.core = JetBeans.getInstance(project);
        this.classes = new ClassesPanel(this, project);
        this.instances = new InstancesPanel(this, project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        project.getService(LibraryView.class).initToolWindow(toolWindow);
    }

    public void initToolWindow(ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content classes = contentFactory.createContent(this.classes, "Classes", false);
        Content instances = contentFactory.createContent(this.instances, "Instances", false);
        toolWindow.getContentManager().addContent(classes);
        toolWindow.getContentManager().addContent(instances);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("activate")) toolWindow.show(null);
            if (e.getActionCommand().equals("deactivate")) toolWindow.hide(null);
        });
    }
}

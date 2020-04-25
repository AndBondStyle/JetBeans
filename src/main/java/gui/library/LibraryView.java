package gui.library;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.Content;

@Service
public final class LibraryView {
    public Project project;
    public ClassesPanel classes;
    public InstancesPanel instances;

    public LibraryView(Project project) {
        this.project = project;
        this.classes = new ClassesPanel(this);
        this.instances = new InstancesPanel(this);
    }

    public void initToolWindow(ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content classes = contentFactory.createContent(this.classes, "Classes", false);
        Content instances = contentFactory.createContent(this.instances, "Instances", false);
        toolWindow.getContentManager().addContent(classes);
        toolWindow.getContentManager().addContent(instances);
    }
}

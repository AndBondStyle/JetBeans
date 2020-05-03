package gui.propeditor;

import core.JetBeans;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.Content;
import gui.propeditor.tree.PropertyTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Service
public final class PropertyEditorView implements ToolWindowFactory, DumbAware {
    public JetBeans core;
    public PropertyPanel props;

    public PropertyEditorView() {}

    public PropertyEditorView(Project project) {
        this.core = JetBeans.getInstance(project);
        this.props = new PropertyPanel(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        project.getService(PropertyEditorView.class).initToolWindow(toolWindow);
    }

    public void initToolWindow(ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content props = contentFactory.createContent(this.props, "Properties", false);
        toolWindow.getContentManager().addContent(props);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("activate")) toolWindow.show(null);
            if (e.getActionCommand().equals("deactivate")) toolWindow.hide(null);
        });
    }
}

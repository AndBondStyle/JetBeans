package ide;

import gui.library.LibraryView;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class LibraryToolWindowFactory implements ToolWindowFactory, DumbAware {
    public static String TOOL_WINDOW_ID = "Bean Library";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ServiceManager.getService(project, LibraryView.class).initToolWindow(toolWindow);
    }
}

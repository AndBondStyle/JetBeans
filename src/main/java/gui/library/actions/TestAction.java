package gui.library.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import ide.CustomFileType;
import org.jetbrains.annotations.NotNull;

public class TestAction extends AnAction implements DumbAware {
    public TestAction() {
        super("Test Action", "Performs test action", CustomFileType.ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("Test action");
    }
}

package gui.inspector.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class BindAction extends AnAction implements DumbAware {
    public BindAction() {
        this.getTemplatePresentation().setIcon(AllIcons.Nodes.ExceptionClass);
        this.getTemplatePresentation().setText("Bind Property");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("BIND ACTION");
    }
}

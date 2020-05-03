package gui.propeditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class ShellInputAction extends AnAction implements DumbAware {
    public ShellInputAction() {
        this.getTemplatePresentation().setIcon(AllIcons.Nodes.Console);
        this.getTemplatePresentation().setDescription("Enter from shell");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("SHELL INPUT ACTION");
    }
}

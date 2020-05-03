package gui.propeditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import gui.propeditor.editors.Editor;
import gui.propeditor.shell.ShellInputDialog;
import org.jetbrains.annotations.NotNull;

public class ShellInputAction extends AnAction implements DumbAware {
    private Editor<?> editor;

    public ShellInputAction(Editor<?> editor) {
        this.editor = editor;
        this.getTemplatePresentation().setIcon(AllIcons.Nodes.Console);
        this.getTemplatePresentation().setDescription("Enter from shell");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShellInputDialog dialog = new ShellInputDialog(e.getProject(), this.editor);
        boolean ok = dialog.showAndGet();
        System.out.println("OK: " + ok);
    }
}

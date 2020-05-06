package gui.inspector.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import gui.inspector.editors.Editor;
import gui.inspector.ShellInputDialog;
import org.jetbrains.annotations.NotNull;

public class ShellInputAction extends AnAction implements DumbAware {
    private Editor editor;

    public ShellInputAction(Editor editor) {
        this.editor = editor;
        this.getTemplatePresentation().setIcon(AllIcons.Nodes.Console);
        this.getTemplatePresentation().setText("Enter From Shell");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShellInputDialog dialog = new ShellInputDialog(e.getProject(), this.editor);
        dialog.show();
    }
}

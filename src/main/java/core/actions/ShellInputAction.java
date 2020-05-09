package core.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import gui.inspector.editors.Editor;
import gui.common.ShellInputDialog;
import org.jetbrains.annotations.NotNull;

public class ShellInputAction extends DumbAwareAction {
    private Editor editor;

    public ShellInputAction(Editor editor) {
        this.editor = editor;
        this.getTemplatePresentation().setIcon(AllIcons.Nodes.Console);
        this.getTemplatePresentation().setText("Enter From Shell");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String title = "Shell Input - Property \"" + this.editor.prop.name + "\" - " + this.editor.prop.type.getCanonicalName();
        ShellInputDialog dialog = new ShellInputDialog(e.getProject(), title);
        dialog.evaluator.setReturnType(this.editor.prop.type);
        dialog.callback = () -> this.editor.accept(dialog.result, true);
        dialog.evaluator.setParameters(
                new String[] { "value" },
                new String[] { "current property value" },
                new Class[] { this.editor.prop.type },
                new Object[] { this.editor.prop.getter.get() }
        );
        dialog.init();
        dialog.show();
    }
}

package core.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import core.main.JetBeans;
import gui.inspector.editors.Editor;
import gui.common.ShellInputDialog;
import gui.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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
        JetBeans core = JetBeans.getInstance(e.getProject());
        if (core.getCanvas() == null) return;
        List<Object> context = core.getCanvas().items.stream()
                .filter(x -> x instanceof Wrapper)
                .map(x -> ((Wrapper) x).getTarget())
                .collect(Collectors.toList());
        dialog.evaluator.setParameters(
                new String[] { "scene", "bean", "prop" },
                new String[] { "references to all items on the scene", "currently selected bean", "current property value" },
                new Class[] { context.getClass(), this.editor.prop.target.getClass(), this.editor.prop.type },
                new Object[] { context, this.editor.prop.target, this.editor.prop.getter.get() }
        );
        dialog.init();
        dialog.show();
    }
}

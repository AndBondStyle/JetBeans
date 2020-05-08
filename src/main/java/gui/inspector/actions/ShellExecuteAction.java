package gui.inspector.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import core.JetBeans;
import gui.common.ShellInputDialog;
import gui.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ShellExecuteAction extends DumbAwareAction {
    public ShellExecuteAction() {
        this.getTemplatePresentation().setIcon(AllIcons.Nodes.Console);
        this.getTemplatePresentation().setText("Execute Anything");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShellInputDialog dialog = new ShellInputDialog(e.getProject(), "Random Shell Input");
        dialog.evaluator.setReturnType(null);
        dialog.evaluator.setBody("\n\n");
        dialog.offset = 1;
        dialog.callback = () -> this.actionPerformed(e);
        JetBeans core = JetBeans.getInstance(e.getProject());
        List<Object> context = core.getCanvas().items.stream()
                .filter(x -> x instanceof Wrapper)
                .collect(Collectors.toList());
        if (core.selection instanceof Wrapper) {
            Object target = ((Wrapper) core.selection).getTarget();
            dialog.evaluator.setParameters(
                    new String[] {"selection", "context"},
                    new String[] {"currently selected object", "access other objects via context.get(<index>)"},
                    new Class[] {target.getClass(), context.getClass()},
                    new Object[] {target, context}
            );
        } else {
            dialog.evaluator.setParameters(
                    new String[] {"context"},
                    new String[] {"access other objects via \"context.get(<index>)\""},
                    new Class[] {context.getClass()},
                    new Object[] {context}
            );
        }
        dialog.init();
        dialog.show();
    }
}

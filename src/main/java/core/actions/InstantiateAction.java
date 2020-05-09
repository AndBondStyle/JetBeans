package core.actions;

import com.intellij.openapi.project.DumbAware;
import gui.library.LibraryView;
import core.main.JetBeans;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

public class InstantiateAction extends AnAction implements DumbAware {
    public InstantiateAction() {
        super("Instantiate Class", "Create new instance of selected class", AllIcons.Nodes.Interface);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        JetBeans core = JetBeans.getInstance(e.getProject());
        if (core.getCanvas() == null) return;
        String klass = LibraryView.getClassName(e.getProject());
        core.instantiate(klass, null, false);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        JetBeans core = JetBeans.getInstance(e.getProject());
        e.getPresentation().setEnabled(core.getCanvas() != null);
    }
}

package gui.library.actions;

import com.intellij.openapi.project.DumbAware;
import gui.common.ShellInputDialog;
import gui.common.tree.PatchedNode;
import gui.library.LibraryView;
import core.JetBeans;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import gui.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class InstantiateAction extends AnAction implements DumbAware {
    public InstantiateAction() {
        super("Instantiate Class", "Create new instance of selected class", AllIcons.Nodes.Interface);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        JetBeans core = JetBeans.getInstance(e.getProject());
        String klass = LibraryView.getClassID(e.getProject());
        core.instantiate(klass, null, false);
    }
}

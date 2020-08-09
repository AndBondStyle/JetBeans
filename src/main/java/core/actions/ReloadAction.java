package core.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import core.main.JetBeans;
import gui.common.tree.PatchedNode;
import gui.library.LibraryView;
import org.jetbrains.annotations.NotNull;

public class ReloadAction extends DumbAwareAction {
    public ReloadAction() {
        super("Reload Item", "Reload classes from disk", AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        PatchedNode node = LibraryView.getActiveNode(e.getProject());
        if (node == null) return;
        JetBeans core = JetBeans.getInstance(e.getProject());
        try { core.loader.reload(node); }
        catch (Exception err) { core.logException(err, "Reload failed"); }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        e.getPresentation().setEnabled(false);
        if (LibraryView.getInstance(e.getProject()).getActiveTab().equals(LibraryView.INSTANCES_TAB)) return;
        PatchedNode node = LibraryView.getActiveNode(e.getProject());
        if (node == null) return;
        if (node.getValue() != null) e.getPresentation().setEnabled(true);
    }
}

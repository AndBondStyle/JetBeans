package core.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import core.main.JetBeans;
import gui.common.tree.PatchedNode;
import gui.library.LibraryView;
import org.jetbrains.annotations.NotNull;

public class DeleteAction extends DumbAwareAction {
    public DeleteAction() {
        super("Remove Item", "Remove item", AllIcons.Actions.GC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        String tab = LibraryView.getInstance(e.getProject()).getActiveTab();
        if (tab.equals(LibraryView.CLASSES_TAB)) {
            PatchedNode node = LibraryView.getActiveNode(e.getProject());
            if (node == null) return;
            JetBeans core = JetBeans.getInstance(e.getProject());
            core.loader.delete(node);
        } else {
            JetBeans core =  JetBeans.getInstance(e.getProject());
            if (core.selection != null && core.getCanvas() != null) {
                core.getCanvas().removeItem(core.selection);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        e.getPresentation().setEnabled(false);
        String tab = LibraryView.getInstance(e.getProject()).getActiveTab();
        if (tab.equals(LibraryView.CLASSES_TAB)) {
            e.getPresentation().setVisible(true);
            PatchedNode node = LibraryView.getActiveNode(e.getProject());
            if (node == null) return;
            if (node.getValue() != null) e.getPresentation().setEnabled(true);
        } else {
            JetBeans core =  JetBeans.getInstance(e.getProject());
            if (core.selection != null && core.getCanvas() != null) e.getPresentation().setEnabled(true);
        }
    }
}

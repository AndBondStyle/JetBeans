package gui.inspector.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import core.JetBeans;
import core.links.Linker;
import gui.inspector.InspectorView;
import org.jetbrains.annotations.NotNull;

public class LinkAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Object item = e.getProject().getService(InspectorView.class).getActiveItem();
        Linker linker = JetBeans.getInstance(e.getProject()).linker;
        linker.accept(linker.isAcceptable(item) ? item : null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Object item = e.getProject().getService(InspectorView.class).getActiveItem();
        Linker linker = JetBeans.getInstance(e.getProject()).linker;
        e.getPresentation().setIcon(AllIcons.Nodes.ExceptionClass);
        if (linker.isAcceptable(item)) {
            e.getPresentation().setEnabled(true);
            e.getPresentation().setText(linker.active ? "Finish Link" : "Create Link");
        } else {
            if (!linker.active) {
                e.getPresentation().setEnabled(false);
                e.getPresentation().setText("Create Link");
            } else {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setIcon(AllIcons.Actions.Cancel);
                e.getPresentation().setText("Cancel");
            }
        }
    }
}

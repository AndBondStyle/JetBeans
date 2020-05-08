package gui.inspector.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import core.JetBeans;
import core.links.LinkBase;
import core.links.Linker;
import gui.inspector.InspectorView;
import gui.link.Link;
import org.jetbrains.annotations.NotNull;

public class LinkAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JetBeans core = JetBeans.getInstance(e.getProject());
        if (core.selection instanceof Link) {
            Object link = ((Link) core.selection).descriptor;
            if (link instanceof LinkBase) ((LinkBase) link).init(null);
        } else {
            Object item = e.getProject().getService(InspectorView.class).getActiveItem();
            Linker linker = JetBeans.getInstance(e.getProject()).linker;
            linker.accept(linker.isAcceptable(item) ? item : null);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Object item = e.getProject().getService(InspectorView.class).getActiveItem();
        JetBeans core = JetBeans.getInstance(e.getProject());
        e.getPresentation().setIcon(AllIcons.Nodes.ExceptionClass);
        if (core.linker.isAcceptable(item)) {
            e.getPresentation().setEnabled(true);
            e.getPresentation().setText(core.linker.active ? "Finish Link" : "Create Link");
        } else {
            if (!core.linker.active) {
                if (core.selection instanceof Link) {
                    e.getPresentation().setEnabled(true);
                    e.getPresentation().setIcon(AllIcons.Actions.Edit);
                    e.getPresentation().setText("Edit Link");
                } else {
                    e.getPresentation().setEnabled(false);
                    e.getPresentation().setText("Create Link");
                }
            } else {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setIcon(AllIcons.Actions.Cancel);
                e.getPresentation().setText("Cancel");
            }
        }
    }
}

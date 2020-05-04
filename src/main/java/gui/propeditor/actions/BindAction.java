package gui.propeditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import gui.propeditor.editors.Editor;
import gui.propeditor.tree.PropertyTree;
import org.jetbrains.annotations.NotNull;

public class BindAction extends AnAction implements DumbAware {
    private PropertyTree tree;

    public BindAction(PropertyTree tree) {
        this.tree = tree;
        this.getTemplatePresentation().setIcon(AllIcons.Actions.Lightning);
        this.getTemplatePresentation().setDescription("Bind property");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("BIND ACTION");
    }
}

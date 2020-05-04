package gui.propeditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.ui.tree.TreeUtil;
import gui.propeditor.tree.PropertyTree;
import org.jetbrains.annotations.NotNull;

public class CollapseAllAction extends AnAction implements DumbAware {
    private PropertyTree tree;

    public CollapseAllAction(PropertyTree tree) {
        this.tree = tree;
        this.getTemplatePresentation().setIcon(AllIcons.Actions.Collapseall);
        this.getTemplatePresentation().setDescription("Collapse all");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreeUtil.collapseAll(this.tree, 0);
    }
}

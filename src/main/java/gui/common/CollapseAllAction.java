package gui.common;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CollapseAllAction extends AnAction implements DumbAware {
    private JTree tree;

    public CollapseAllAction(JTree tree) {
        this.tree = tree;
        this.getTemplatePresentation().setText("Collapse All");
        this.getTemplatePresentation().setIcon(AllIcons.Actions.Collapseall);
        this.getTemplatePresentation().setDescription("Collapse all");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreeUtil.collapseAll(this.tree, 0);
    }
}

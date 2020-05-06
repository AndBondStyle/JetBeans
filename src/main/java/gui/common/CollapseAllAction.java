package gui.common;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public class CollapseAllAction extends AnAction implements DumbAware {
    private Supplier<JTree> treeSupplier;

    public CollapseAllAction(JTree tree) {
        this(() -> tree);
    }

    public CollapseAllAction(Supplier<JTree> treeSupplier) {
        this.treeSupplier = treeSupplier;
        this.getTemplatePresentation().setText("Collapse All");
        this.getTemplatePresentation().setIcon(AllIcons.Actions.Collapseall);
        this.getTemplatePresentation().setDescription("Collapse all");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JTree tree = this.treeSupplier.get();
        if (tree == null) return;
        TreeUtil.collapseAll(tree, 0);
    }
}

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
        String klass = this.getClassID(e.getProject());
        if (klass == null) return;
        ShellInputDialog dialog = new ShellInputDialog(e.getProject(), "Shell Input - New Instance");
        dialog.evaluator.setReturnType(Object.class);
        dialog.evaluator.setBody("\nreturn new " + klass + "();\n");
        dialog.offset = 3;
        JetBeans core = JetBeans.getInstance(e.getProject());
        dialog.callback = () -> {
            Wrapper wrapper = Wrapper.autowrap(dialog.result);
            core.getCanvas().addItem(wrapper);
            core.getCanvas().setSelection(wrapper);
            core.fireEvent("instantiate");
        };
        dialog.init();
        dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        String klass = this.getClassID(e.getProject());
        e.getPresentation().setEnabled(klass != null);
    }

    private String getClassID(Project project) {
        LibraryView view = project.getService(LibraryView.class);
        if (!view.getActiveTab().equals(LibraryView.CLASSES_TAB)) return null;
        TreePath path = view.classes.tree.getSelectionPath();
        if (path == null) return null;
        PatchedNode node = (PatchedNode) path.getLastPathComponent();
        return node.getKey().startsWith("!") ? null : node.getKey();
    }
}

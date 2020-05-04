package gui.common.tree;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Collections;

public class PatchedDescriptor extends AbstractTreeNode<Object> {
    protected PatchedDescriptor(Project project, @NotNull Object value) {
        super(project, value);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode<Object>> getChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {}
}

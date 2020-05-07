package gui.common.tree;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;

public class PatchedNode extends DefaultMutableTreeNode {
    PresentationData presentation;

    public PatchedNode(Project project, String key) {
        this(project, key, null);
    }

    public PatchedNode(Project project, String key, Object value) {
        PatchedDescriptor descriptor = new PatchedDescriptor(project, new Pair<>(key, value));
        this.setUserObject(descriptor);
        this.presentation = descriptor.getPresentation();
        this.setPrimaryText(key);
    }

    public String getKey() {
        PatchedDescriptor descriptor = (PatchedDescriptor) this.getUserObject();
        return (String) ((Pair<?, ?>) descriptor.getValue()).getFirst();
    }

    public Object getValue() {
        PatchedDescriptor descriptor = (PatchedDescriptor) this.getUserObject();
        return ((Pair<?, ?>) descriptor.getValue()).getSecond();
    }

    public void setPrimaryText(String text) {
        this.presentation.setPresentableText(text);
    }

    public void setSecondaryText(String text) {
        this.presentation.setLocationString(text);
    }

    public void setIcon(Icon icon) {
        this.presentation.setIcon(icon);
    }
}

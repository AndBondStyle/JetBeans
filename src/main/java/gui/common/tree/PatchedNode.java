package gui.common.tree;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class PatchedNode extends DefaultMutableTreeNode {
    PresentationData presentation;

    public PatchedNode(Project project, Object data) {
        PatchedDescriptor descriptor = new PatchedDescriptor(project, data);
        this.setUserObject(descriptor);
        this.presentation = descriptor.getPresentation();
    }

    public String getData() {
        return this.getRawData().toString();
    }

    public Object getRawData() {
        return ((PatchedDescriptor) this.getUserObject()).getValue();
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

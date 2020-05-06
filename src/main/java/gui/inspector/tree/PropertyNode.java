package gui.inspector.tree;

import com.intellij.openapi.project.Project;
import gui.common.tree.PatchedNode;
import gui.inspector.editors.Editor;

public class PropertyNode extends PatchedNode {
    public Editor editor;

    public PropertyNode(Editor editor, Project project) {
        // TODO: Maybe generate ID for editor?
        super(project, "");
        this.editor = editor;
    }
}

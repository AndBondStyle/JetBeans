package gui.propeditor.tree;

import com.intellij.openapi.project.Project;
import gui.common.tree.PatchedNode;
import gui.propeditor.editors.Editor;

public class PropertyNode extends PatchedNode {
    private Editor<?> editor;

    public PropertyNode(Editor<?> editor, Project project) {
        super(project, "");
        this.editor = editor;
    }

    public Editor<?> getEditor() {
        return this.editor;
    }
}

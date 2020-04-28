package core;

import gui.common.SimpleEventSupport;
import core.registry.Registry;
import gui.canvas.CanvasItem;
import gui.canvas.Canvas;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import ide.CustomFileEditor;

@Service
public final class JetBeans implements SimpleEventSupport {
    private Project project;
    private Registry registry;
    private CanvasItem selection;

    private CustomFileEditor prevEditor;
    private CustomFileEditor currEditor;

    public JetBeans(Project project) {
        this.project = project;
        this.registry = new Registry();
    }

    public static JetBeans getInstance(Project project) {
        return project.getService(JetBeans.class);
    }

    public Project getProject() {
        return this.project;
    }

    public Registry getRegistry() {
        return this.registry;
    }

    public CanvasItem getSelection() {
        return this.selection;
    }

    public void setSelection(CanvasItem selection) {
        this.selection = selection;
        this.fireEvent("select");
    }

    public Canvas getCanvas() {
        if (this.currEditor == null) return null;
        return this.currEditor.getCanvas();
    }

    public void registerEditor(CustomFileEditor editor) {
        editor.addListener(e -> {
            if (e.getActionCommand().equals("activate")) {
                if (this.currEditor != editor) this.prevEditor = this.currEditor;
                this.currEditor = (CustomFileEditor) e.getSource();
                this.editorUpdated();
            }
            if (e.getActionCommand().equals("deactivate")) {
                if (editor == this.currEditor) {
                    this.prevEditor = this.currEditor;
                    this.currEditor = null;
                }
                this.editorUpdated();
            }
        });
        editor.getCanvas().addListener(e -> {
            if (e.getActionCommand().equals("select")) {
                if (this.currEditor == editor) {
                    CanvasItem selection = editor.getCanvas().getSelection();
                    this.setSelection(selection);
                }
            }
        });
    }

    private void editorUpdated() {
        if (this.currEditor == null && this.prevEditor != null) {
            if (this.prevEditor.isActive()) {
                this.currEditor = this.prevEditor;
                this.prevEditor = null;
            }
        }
        if (this.currEditor != null) {
            CanvasItem selection = this.currEditor.getCanvas().getSelection();
            this.setSelection(selection);
            this.fireEvent("activate");
        } else {
            this.setSelection(null);
            this.fireEvent("deactivate");
        }
    }
}
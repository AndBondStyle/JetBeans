package core;

import core.links.Linker;
import core.registry.MasterLoader;
import gui.common.SimpleEventSupport;
import gui.canvas.CanvasItem;
import gui.canvas.Canvas;
import gui.wrapper.Wrapper;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import ide.CustomFileEditor;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public final class JetBeans implements SimpleEventSupport {
    public static String PLUGIN_ID = "jetbeans.jetbeans";

    public Project project;
    public CanvasItem selection;
    public Linker linker;
    public MasterLoader loader;

    private CustomFileEditor prevEditor;
    private CustomFileEditor currEditor;

    public JetBeans(Project project) {
        this.project = project;
        this.linker = new Linker(this);
        this.loader = new MasterLoader();
    }

    public static JetBeans getInstance(Project project) {
        return project.getService(JetBeans.class);
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
            if (this.currEditor == editor) {
                if (e.getActionCommand().equals("select")) {
                    CanvasItem selection = editor.getCanvas().getSelection();
                    this.setSelection(selection);
                }
                if (e.getActionCommand().equals("itemsChanged")) {
                    this.fireEvent("itemsChanged");
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
            if (this.linker.active) this.linker.accept(null);
            this.setSelection(selection);
            this.fireEvent("activate");
        } else {
            this.setSelection(null);
            this.fireEvent("deactivate");
        }
    }

    public void instantiate(String name) {
        try {
            Class<?> klass = this.loader.loadClass(name);
            Object instance = klass.getDeclaredConstructor().newInstance();
            Wrapper wrapper = Wrapper.autowrap(instance);
            this.getCanvas().addItem(wrapper);
            this.getCanvas().setSelection(wrapper);
            this.fireEvent("instantiate");
        } catch (Exception e) {
            this.logException(new RuntimeException("Failed to instantiate class \"" + name + "\"", e));
            throw new RuntimeException("Failed to instantiate class \"" + name + "\"", e);
        }
    }

    public void logException(Exception e) {
        Notification n = new Notification("JetBeans", null, NotificationType.ERROR);
        n.setTitle("Bean instantiation failed");
        n.setContent(e.getMessage());
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        e.printStackTrace(printer);
        n.setSubtitle(writer.toString());
        Notifications.Bus.notify(n, this.project);
    }
}

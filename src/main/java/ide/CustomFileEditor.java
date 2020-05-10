package ide;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.WriteAction;
import core.serialization.SceneState;
import gui.common.SimpleEventSupport;
import gui.canvas.Canvas;
import core.main.JetBeans;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.*;

public class CustomFileEditor extends UserDataHolderBase implements FileEditor, SimpleEventSupport {
    private boolean isActive = false;
    private VirtualFile file;
    private JetBeans core;
    private Canvas canvas;

    public CustomFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.canvas = new Canvas(project);
        this.core = JetBeans.getInstance(project);
        this.core.registerEditor(this);
        this.load();
    }

    public void load() {
        try {
            SceneState state = new SceneState();
            byte[] content = this.file.contentsToByteArray();
            state.load(content, this.canvas, this.core);
        } catch (Exception e) {
            e = new RuntimeException("Scene import failed", e);
            this.core.logException(e, "Scene import failed");
        }
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public VirtualFile getFile() {
        return this.file;
    }

    @Override
    public void selectNotify() {
        this.isActive = true;
        this.fireEvent("activate");
    }

    @Override
    public void deselectNotify() {
        this.isActive = false;
        this.fireEvent("deactivate");
    }

    public boolean isActive() {
        return this.isActive;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return this.canvas;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.canvas;
    }

    @NotNull
    @Override
    public String getName() {
        return "JetBeans Editor";
    }

    public void dispose() {
        WriteAction.run(() -> {
            try {
                SceneState state = new SceneState();
                byte[] content = state.dump(this.canvas, this.core);
                this.file.setBinaryContent(content);
                Notification n = new Notification("JetBeans", null, NotificationType.INFORMATION);
                n.setTitle("Scene serialized successfully");
                Notifications.Bus.notify(n, this.core.project);
            } catch (Exception e) {
                e = new RuntimeException("Scene export failed", e);
                this.core.logException(e, "Scene export failed");
            }
        });
    }

    // NOT USED (YET)

    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {}
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {}
    public BackgroundEditorHighlighter getBackgroundHighlighter() { return null; }
    public FileEditorLocation getCurrentLocation() { return null; }
    public void setState(@NotNull FileEditorState state) { }
    public boolean isModified() { return false; }
    public boolean isValid() { return true; }
}

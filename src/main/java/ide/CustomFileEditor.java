package ide;

import gui.MainFrame;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;

public class CustomFileEditor extends UserDataHolderBase implements FileEditor {
    public final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public final Project project;
    public final VirtualFile file;
    public final JComponent component;

    public CustomFileEditor(Project project, VirtualFile file) {
        this.project = project;
        this.file = file;
        this.component = new MainFrame();
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return component;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return component;
    }

    @NotNull
    @Override
    public String getName() {
        return "JetBeans Editor";
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void selectNotify() {
        System.out.println("SELECT");
        ToolWindowManager manager = ToolWindowManager.getInstance(this.project);
        ToolWindow window = manager.getToolWindow(LibraryToolWindowFactory.TOOL_WINDOW_ID);
        if (window != null) window.show(null);
    }

    @Override
    public void deselectNotify() {
        System.out.println("DESELECT");
    }

    // NOT USED (YET)

    public BackgroundEditorHighlighter getBackgroundHighlighter() { return null; }
    public FileEditorLocation getCurrentLocation() { return null; }
    public void setState(@NotNull FileEditorState state) { }
    public boolean isModified() { return false; }
    public boolean isValid() { return true; }
    public void dispose() { }
}

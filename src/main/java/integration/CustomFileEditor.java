package integration;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CustomFileEditor extends UserDataHolderBase implements FileEditor {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Project project;
    private final VirtualFile file;
    private final JComponent component;

    public CustomFileEditor(Project project, VirtualFile file) {
        this.project = project;
        this.file = file;
        this.component = new JBPanel<>();
        this.component.add(new JLabel("TEST FILE EDITOR"));
        this.component.add(new JButton("TEST BUTTON (TOUCHY)"));
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

    // NOT USED (YET)

    public BackgroundEditorHighlighter getBackgroundHighlighter() { return null; }
    public FileEditorLocation getCurrentLocation() { return null; }
    public void setState(@NotNull FileEditorState state) { }
    public boolean isModified() { return false; }
    public boolean isValid() { return true; }
    public void selectNotify() { }
    public void deselectNotify() { }
    public void dispose() { }
}

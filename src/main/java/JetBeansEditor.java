import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JetBeansEditor extends JPanel implements FileEditor {
    private final Project project;
    private final VirtualFile file;

    public JetBeansEditor(Project project, VirtualFile file) {
        this.project = project;
        this.file = file;
        // TODO
        JLabel label = new JLabel("KOK");
        this.add(label);
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        // TODO
        return this;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        // TODO
        return null;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        // TODO
    }

    @Override
    public boolean isModified() {
        // TODO
        return false;
    }

    @Override
    public void selectNotify() {
        // TODO
    }

    @Override
    public void deselectNotify() {
        // TODO
    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        // TODO
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        // TODO
        return null;
    }

    @Override
    public void dispose() {
        // TODO
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        // TODO
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        // TODO
    }
}

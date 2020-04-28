package ide;

import gui.common.SimpleEventSupport;
import gui.wrapper.Wrapper;
import gui.canvas.Canvas;
import gui.link.Link;
import core.JetBeans;

import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.JBColor;
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
import javax.swing.*;
import java.awt.*;

public class CustomFileEditor extends UserDataHolderBase implements FileEditor, SimpleEventSupport {
    private boolean isActive = false;
    private VirtualFile file;
    private JetBeans core;
    private Canvas canvas;

    public CustomFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.canvas = new Canvas();
        this.core = JetBeans.getInstance(project);
        this.core.registerEditor(this);

        // ====== TEMP ======
        Component temp = new JBIntSpinner(50, 0, 100, 1);
        Wrapper wrapper = new Wrapper(temp);
        this.canvas.addItem(wrapper);
        wrapper.setLocation(100, 100);

        for (int i = 0; i < 10; i++) {
            Component temp2 = new JBIntSpinner(50, 0, 100, 1);
            Wrapper wrapper2 = new Wrapper(temp2);
            this.canvas.addItem(wrapper2);
            wrapper2.setLocation(100, 200 + 100 * i);

            Link link = new Link(JBColor.MAGENTA);
            this.canvas.addItem(link);
            wrapper.attachLink(link, 0);
            wrapper2.attachLink(link, 1);
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

    // NOT USED (YET)

    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {}
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {}
    public BackgroundEditorHighlighter getBackgroundHighlighter() { return null; }
    public FileEditorLocation getCurrentLocation() { return null; }
    public void setState(@NotNull FileEditorState state) { }
    public boolean isModified() { return false; }
    public boolean isValid() { return true; }
    public void dispose() {}
}

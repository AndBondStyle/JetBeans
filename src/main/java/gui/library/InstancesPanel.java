package gui.library;

import core.JetBeans;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;

public class InstancesPanel extends SimpleToolWindowPanel {
    public LibraryView parent;
    public JetBeans core;

    public InstancesPanel(LibraryView parent, Project project) {
        super(false, true);
        this.parent = parent;
        this.core = JetBeans.getInstance(project);

        JLabel label = new JLabel();
        this.setContent(label);
        this.core.addListener(e -> {
            if (e.getActionCommand().equals("select")) {
                Object selection = this.core.getSelection();
                label.setText(selection == null ? "NULL" : selection.toString());
                label.repaint();
            }
        });
    }
}

package gui.library;

import com.intellij.openapi.ui.SimpleToolWindowPanel;

public class InstancesPanel extends SimpleToolWindowPanel {
    public LibraryView parent;

    public InstancesPanel(LibraryView parent) {
        super(false, true);
        this.parent = parent;
    }
}

package gui.propeditor.editors;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import gui.propeditor.actions.ShellInputAction;

import javax.swing.*;
import java.awt.*;

public abstract class EditorUI<T> extends Editor<T> {
    protected JPanel namePanel;
    protected JPanel centerPanel;
    protected JPanel buttonsPanel;
    protected JBSplitter splitter;
    protected SimpleColoredComponent nameLabel;
    protected ActionButton shellButton;

    @Override
    public void init() {
        this.buildAll();
        super.init();
    }

    protected void buildAll() {
        this.buildPanels();
        this.populateNamePanel();
        this.populateCenterPanel();
        this.populateButtonsPanel();

        if (this.setter == null) {
            this.nameLabel.setIcon(AllIcons.Diff.Lock);
            this.shellButton.setEnabled(false);
        }
    }

    protected void buildPanels() {
        this.namePanel = new JPanel();
        this.namePanel.setLayout(new BoxLayout(this.namePanel, BoxLayout.LINE_AXIS));
        this.namePanel.setMinimumSize(new Dimension());
        this.centerPanel = new JPanel();
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.LINE_AXIS));
        this.centerPanel.setMinimumSize(new Dimension());
        this.buttonsPanel = new JPanel();
        this.buttonsPanel.setLayout(new BoxLayout(this.buttonsPanel, BoxLayout.LINE_AXIS));
        this.splitter = new JBSplitter(false, 0.5f);
        this.splitter.setFirstComponent(this.namePanel);
        this.splitter.setSecondComponent(this.centerPanel);
        this.splitter.addPropertyChangeListener("proportion", e -> this.parent.updateSplitters((float) e.getNewValue()));
        this.setLayout(new BorderLayout());
        this.add(this.splitter, BorderLayout.CENTER);
        this.add(this.buttonsPanel, BorderLayout.EAST);
    }
    
    protected void populateNamePanel() {
        this.nameLabel = new SimpleColoredComponent();
        this.nameLabel.setOpaque(false);
        this.nameLabel.append(this.name);
        this.nameLabel.append("   ");
        this.nameLabel.append(this.type.getSimpleName(), SimpleTextAttributes.GRAYED_ATTRIBUTES);
        this.nameLabel.setToolTipText(this.type.getCanonicalName() + (this.setter == null ? " (readonly)" : ""));
        this.namePanel.add(this.nameLabel);
    }
    
    protected void populateCenterPanel() {
        // Main editor widget should be initialized here...
    }

    protected void populateButtonsPanel() {
        AnAction action = new ShellInputAction(this);
        this.shellButton = new ActionButton(
                action,
                action.getTemplatePresentation().clone(),
                ActionPlaces.UNKNOWN,
                ActionToolbar.NAVBAR_MINIMUM_BUTTON_SIZE
        );
        this.shellButton.setMaximumSize(new Dimension(50, 1000));
        this.buttonsPanel.add(this.shellButton);
    }

    public void updateSplitter(float proportion) {
        this.splitter.setProportion(proportion);
    }
}

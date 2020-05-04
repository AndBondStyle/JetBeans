package gui.propeditor.editors;

import gui.propeditor.actions.ShellInputAction;
import gui.propeditor.tree.PropertyTree;
import core.PropertyInfo;

import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.UIUtil;
import com.intellij.icons.AllIcons;

import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public abstract class Editor extends JPanel {
    private static HashMap<Class<?>, Class<?>> editors = new HashMap<>();

    static {
        Editor.registerEditor(Object.class, ObjectEditor.class);
    }

    public static void registerEditor(Class<?> propClass, Class<?> editorClass) {
        Editor.editors.put(propClass, editorClass);
    }

    public static Editor createEditor(PropertyTree parent, PropertyDescriptor descriptor, Object target) {
        PropertyInfo prop = PropertyInfo.create(descriptor, target);
        if (prop == null) return null;
        Class<?> editorClass = Editor.editors.get(prop.type);
        if (editorClass == null) editorClass = Editor.editors.get(Object.class);
        try {
            Editor editor = (Editor) editorClass.getConstructor().newInstance();
            editor.parent = parent;
            editor.prop = prop;
            return editor;
        } catch (ReflectiveOperationException e) {
            String message = "Failed to create editor for property\"" + prop.name + "\"";
            throw new RuntimeException(message, e);
        }
    }

    public PropertyChangeListener listener;
    public PropertyTree parent;
    public PropertyInfo prop;
    public Object value;

    protected JPanel namePanel;
    protected JPanel centerPanel;
    protected JPanel buttonsPanel;
    protected JBSplitter splitter;
    protected SimpleColoredComponent nameLabel;
    protected ActionButton shellButton;

    public void init() {
        this.buildUI();
        if (this.prop.target instanceof Component) {
            Component comp = (Component) this.prop.target;
            this.listener = e -> this.accept(e.getNewValue(), false);
            comp.addPropertyChangeListener(this.prop.name, this.listener);
            this.accept(this.prop.getter.get(), false);
            this.update();
        }
    }

    public void dispose() {
        if (this.prop.target instanceof Component) {
            Component comp = (Component) this.prop.target;
            comp.removePropertyChangeListener(this.prop.name, this.listener);
        }
    }

    public void accept(Object value, boolean forward) {
        if (Objects.equals(value, this.value)) return;
        this.value = value;
        if (forward) this.prop.setter.accept(value);
        this.update();
    }

    protected void buildUI() {
        this.buildPanels();
        this.populateNamePanel();
        this.populateCenterPanel();
        this.populateButtonsPanel();

        if (!this.prop.isSettable()) {
            this.nameLabel.setIcon(AllIcons.Diff.Lock);
            this.shellButton.setEnabled(false);
        }
    }

    protected void buildPanels() {
        this.namePanel = new JPanel();
        this.namePanel.setOpaque(false);
        this.namePanel.setLayout(new BoxLayout(this.namePanel, BoxLayout.LINE_AXIS));
        this.namePanel.setMinimumSize(new Dimension());

        this.centerPanel = new JPanel();
        this.centerPanel.setOpaque(false);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.LINE_AXIS));
        this.centerPanel.setMinimumSize(new Dimension());

        this.buttonsPanel = new JPanel();
        this.buttonsPanel.setOpaque(false);
        this.buttonsPanel.setLayout(new BoxLayout(this.buttonsPanel, BoxLayout.LINE_AXIS));

        this.splitter = new JBSplitter(false, 0.5f);
        this.splitter.setFirstComponent(this.namePanel);
        this.splitter.setSecondComponent(this.centerPanel);
        this.splitter.addPropertyChangeListener("proportion", e -> this.parent.updateSplitters((float) e.getNewValue()));
        this.splitter.setBackground(UIUtil.getTreeSelectionBackground(true));
        this.splitter.setForeground(UIUtil.getTreeSelectionBackground(true));
        this.splitter.setOpaque(false);

        this.setLayout(new BorderLayout());
        this.add(this.splitter, BorderLayout.CENTER);
        this.add(this.buttonsPanel, BorderLayout.EAST);
        this.setBackground(UIUtil.getTreeSelectionBackground(true));
        this.setFocusable(true);
        this.setOpaque(false);
        this.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { Editor.this.setOpaque(true); }
            public void focusLost(FocusEvent e) { Editor.this.setOpaque(false); }
        });
    }

    protected void populateNamePanel() {
        this.nameLabel = new SimpleColoredComponent();
        this.nameLabel.setOpaque(false);
        this.nameLabel.append(this.prop.name);
        this.nameLabel.append("   ");
        this.nameLabel.append(this.prop.type.getSimpleName(), SimpleTextAttributes.GRAYED_ATTRIBUTES);
        String tooltip = this.prop.type.getCanonicalName();
        if (!this.prop.isSettable()) tooltip += " (readonly)";
        this.nameLabel.setToolTipText(tooltip);
        this.namePanel.add(this.nameLabel);
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

    protected void populateCenterPanel() {
        // Main editor widget should be initialized here...
    }

    protected void update() {
        // Update GUI based on current value...
    }
}

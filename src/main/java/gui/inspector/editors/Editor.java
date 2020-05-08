package gui.inspector.editors;

import gui.common.tree.PatchedNode;
import gui.inspector.actions.ShellInputAction;
import gui.inspector.tree.PropertyNode;
import gui.inspector.tree.PropertyTree;
import core.inspection.PropertyInfo;

import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.IconUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ui.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Objects;

public abstract class Editor extends JPanel {
    private static HashMap<Class<?>, Class<?>> editors = new HashMap<>();
    private static Icon LOCK = AllIcons.Nodes.C_private;

    static {
        Editor.registerEditor(Object.class, ObjectEditor.class);
    }

    public static void registerEditor(Class<?> propClass, Class<?> editorClass) {
        Editor.editors.put(propClass, editorClass);
    }

    public static Editor createEditor(PropertyTree parent, PropertyInfo prop) {
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

        MouseListener listener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
                    Editor.this.mainAction();
            }
        };
        this.addMouseListener(listener);
        this.namePanel.addMouseListener(listener);
        this.nameLabel.addMouseListener(listener);
        this.centerPanel.addMouseListener(listener);
        this.buttonsPanel.addMouseListener(listener);
        this.splitter.addMouseListener(listener);

        Icon icon = AllIcons.Nodes.Property;
        if (!this.prop.isBound()) icon = IconUtil.desaturate(icon);
        if (!this.prop.isSettable()) {
            icon = LayeredIcon.create(icon, LOCK);
            this.shellButton.setEnabled(false);
        }
        this.nameLabel.setIcon(icon);
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
        this.splitter.getDivider().setOpaque(false);

        this.setLayout(new BorderLayout());
        this.add(this.splitter, BorderLayout.CENTER);
        this.add(this.buttonsPanel, BorderLayout.EAST);
        this.setFocusable(false);
        this.setOpaque(false);
    }

    protected void populateNamePanel() {
        this.nameLabel = new SimpleColoredComponent();
        this.nameLabel.setOpaque(false);
        this.nameLabel.append(this.prop.name);
        this.nameLabel.append("   ");
        this.nameLabel.append(this.prop.type.getSimpleName(), SimpleTextAttributes.GRAYED_ATTRIBUTES);
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(this.prop.descriptor.getShortDescription());
        if (tooltip.toString().endsWith(".")) tooltip.setLength(tooltip.length() - 1);
        tooltip.append(" - ").append(this.prop.type.getCanonicalName()).append(" - ");
        if (!this.prop.isSettable()) tooltip.append("readonly, ");
        if (this.prop.isBound()) tooltip.append("bound, ");
        if (this.prop.isHidden()) tooltip.append("hidden, ");
        if (this.prop.isExpert()) tooltip.append("expert, ");
        tooltip.setLength(tooltip.length() - 2);
        this.nameLabel.setToolTipText(tooltip.toString());
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

    @Override
    public void paint(Graphics g) {
        Dimension size = g.getClipBounds().getSize();
        this.setPreferredSize(size);
        super.paint(g);
    }

    public void updateSplitter(float proportion) {
        this.splitter.setProportion(proportion);
    }

    public void mainAction() {
        this.shellButton.click();
    }

    protected void populateCenterPanel() {
        // Main editor widget should be initialized here...
    }

    protected void update() {
        // Update GUI based on current value...
    }
}

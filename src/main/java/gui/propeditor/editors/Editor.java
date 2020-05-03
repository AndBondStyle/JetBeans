package gui.propeditor.editors;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import gui.propeditor.actions.ShellInputAction;
import gui.propeditor.tree.PropertyTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Editor<T> extends JPanel {
    private static HashMap<Class<?>, Class<?>> editors = new HashMap<>();

    static {
        Editor.registerEditor(Object.class, ObjectEditor.class);
    }

    protected JPanel namePanel;
    protected JPanel centerPanel;
    protected JPanel buttonsPanel;
    protected JBSplitter splitter;

    public PropertyChangeListener listener;
    public PropertyTree parent;
    public Supplier<T> getter;
    public Consumer<T> setter;
    public Object target;
    public Class<?> type;
    public String name;
    public T value;

    public Editor (PropertyTree parent, Object target, String name, Class<?> type, Supplier<T> getter, Consumer<T> setter) {
        this.parent = parent;
        this.target = target;
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        this.name = name;
    }

    public static void registerEditor(Class<?> propClass, Class<?> editorClass) {
        Editor.editors.put(propClass, editorClass);
    }

    public static Editor<?> createEditor(PropertyTree parent, PropertyDescriptor prop, Object target) {
        Class<?> propType = prop.getPropertyType();
        if (propType == null | prop.isHidden() | prop.isExpert()) return null;
        Class<?> editorClass = Editor.editors.get(propType);
        if (editorClass == null) editorClass = Editor.editors.get(Object.class);
        try {
            Supplier<?> getter = wrapGetter(prop, target);
            if (getter == null) return null;
            Consumer<?> setter = wrapSetter(prop, target);
            return (Editor<?>) editorClass
                // PropertyTree parent, Object target, String name, Class<?> type, Supplier<T> getter, Consumer<T> setter
                .getConstructor(PropertyTree.class, Object.class, String.class, Class.class, Supplier.class, Consumer.class)
                .newInstance(parent, target, prop.getName(), propType, getter, setter);
        } catch (ReflectiveOperationException e) {
            String message = "Failed to create editor for property\"" + prop.getName() + "\"";
            throw new RuntimeException(message, e);
        }
    }

    public static Supplier<?> wrapGetter(PropertyDescriptor prop, Object target) {
        Method read = prop.getReadMethod();
        return (read == null) ? null : () -> {
            try {
                return read.invoke(target);
            } catch (ReflectiveOperationException e) {
                String message = "Failed to call \"" + prop.getName() + "\" property getter";
                throw new RuntimeException(message, e);
            }
        };
    }

    public static Consumer<?> wrapSetter(PropertyDescriptor prop, Object target) {
        Method write = prop.getWriteMethod();
        return (write == null) ? null : (value) -> {
            try {
                write.invoke(target, value);
            } catch (ReflectiveOperationException e) {
                String message = "Failed to call \"" + prop.getName() + "\" property getter";
                throw new RuntimeException(message, e);
            }
        };
    }

    public void init() {
        this.buildAll();
        if (this.target instanceof Component) {
            Component comp = (Component) this.target;
            this.listener = e -> this.accept((T) e.getNewValue(), false);
            comp.addPropertyChangeListener(this.name, this.listener);
            this.accept(this.getter.get(), false);
            this.update();
        }
    }

    public void dispose() {
        if (this.target instanceof Component) {
            Component comp = (Component) this.target;
            comp.removePropertyChangeListener(this.name, this.listener);
        }
    }

    protected void buildAll() {
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

        SimpleColoredComponent label = new SimpleColoredComponent();
        label.setOpaque(false);
        label.append(this.name);
        label.append("   ");
        label.append(this.type.getSimpleName(), SimpleTextAttributes.GRAYED_ATTRIBUTES);
        label.setToolTipText(this.type.getCanonicalName() + (this.setter == null ? " (readonly)" : ""));
        this.namePanel.add(label);

        this.build();

        AnAction action = new ShellInputAction(this);
        ActionButton button = new ActionButton(
                action,
                action.getTemplatePresentation().clone(),
                ActionPlaces.UNKNOWN,
                ActionToolbar.NAVBAR_MINIMUM_BUTTON_SIZE
        );
        button.setMaximumSize(new Dimension(50, 1000));
        this.buttonsPanel.add(button);

        if (this.setter == null) {
            label.setIcon(AllIcons.Diff.Lock);
            button.setEnabled(false);
        }
    }

    public void updateSplitter(float proportion) {
        this.splitter.setProportion(proportion);
    }

    public void accept(Object value, boolean forward) {
        if (value == this.value) return;
        this.value = (T) value;
        if (forward) this.setter.accept((T) value);
        this.update();
    }

    protected void build() {
        // Adds extra GUI elements...
    }

    protected void update() {
        // Update GUI based on current value...
    }
}

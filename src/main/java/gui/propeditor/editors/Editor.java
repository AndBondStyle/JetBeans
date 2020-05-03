package gui.propeditor.editors;

import com.intellij.icons.AllIcons;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import gui.propeditor.tree.PropertyTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    protected PropertyChangeListener listener;
    protected PropertyTree parent;
    protected Supplier<T> getter;
    protected Consumer<T> setter;
    protected Object target;
    protected Class<?> type;
    protected String name;
    protected T value = (T) new Object();

    public Editor (PropertyTree parent, Object target, String name, Class<?> type, Supplier<T> getter, Consumer<T> setter) {
        this.parent = parent;
        this.target = target;
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        this.name = name;
        this.buildAll();
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
        if (this.target instanceof Component) {
            Component comp = (Component) this.target;
            this.listener = e -> this.accept((T) e.getNewValue(), false);
            comp.addPropertyChangeListener(this.name, this.listener);
            this.accept(this.getter.get(), false);
        }
    }

    public void dispose() {
        if (this.target instanceof Component) {
            Component comp = (Component) this.target;
            comp.removePropertyChangeListener(this.name, this.listener);
        }
    }

    protected void buildAll() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        SimpleColoredComponent label = new SimpleColoredComponent();
        label.setOpaque(false);
        label.append(this.name);
        label.append("   ");
        label.append(this.type.getCanonicalName(), SimpleTextAttributes.GRAYED_ATTRIBUTES);
        this.add(label);
        this.build();
        JButton button = new JButton(AllIcons.Nodes.Console);
        button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("SHELL EDITOR ACTION");
            }
        });
        this.add(button);
        if (this.setter == null) {
            label.setIcon(AllIcons.Diff.Lock);
            button.setEnabled(false);
        }
    }

    public void accept(T value, boolean forward) {
        if (value == this.value) return;
        this.value = value;
        if (forward) this.setter.accept(value);
        this.update();
    }

    protected void build() {
        // Adds extra GUI elements...
    }

    protected void update() {
        // Update GUI based on current value...
    }
}

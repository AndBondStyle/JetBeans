package gui.propeditor.editors;

import gui.propeditor.tree.PropertyTree;

import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;

public abstract class Editor<T> extends JPanel {
    private static HashMap<Class<?>, Class<?>> editors = new HashMap<>();

    static {
        Editor.registerEditor(Object.class, ObjectEditor.class);
    }

    public PropertyChangeListener listener;
    public PropertyTree parent;
    public Supplier<T> getter;
    public Consumer<T> setter;
    public Object target;
    public Class<?> type;
    public String name;
    public T value;

    public static void registerEditor(Class<?> propClass, Class<?> editorClass) {
        Editor.editors.put(propClass, editorClass);
    }

    public static Editor<?> createEditor(PropertyTree parent, PropertyDescriptor prop, Object target) {
        Class<?> propType = prop.getPropertyType();
        if (propType == null) return null;
        Class<?> editorClass = Editor.editors.get(propType);
        if (editorClass == null) editorClass = Editor.editors.get(Object.class);
        try {
            Supplier getter = wrapGetter(prop, target);
            if (getter == null) return null;
            Consumer setter = wrapSetter(prop, target);
            Editor<?> editor = (Editor<?>) editorClass.getConstructor().newInstance();
            editor.parent = parent;
            editor.target = target;
            editor.getter = getter;
            editor.setter = setter;
            editor.type = propType;
            editor.name = prop.getName();
            return editor;
        } catch (ReflectiveOperationException e) {
            String message = "Failed to create editor for property\"" + prop.getName() + "\"";
            throw new RuntimeException(message, e);
        }
    }

    public static Supplier wrapGetter(PropertyDescriptor prop, Object target) {
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

    public static Consumer wrapSetter(PropertyDescriptor prop, Object target) {
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
            this.update();
        }
    }

    public void dispose() {
        if (this.target instanceof Component) {
            Component comp = (Component) this.target;
            comp.removePropertyChangeListener(this.name, this.listener);
        }
    }

    public void accept(Object value, boolean forward) {
        if (value == this.value) return;
        this.value = (T) value;
        if (forward) this.setter.accept((T) value);
        this.update();
    }

    protected void update() {
        // Update GUI based on current value...
    }
}

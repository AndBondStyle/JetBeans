package core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyInfo {
    public PropertyDescriptor descriptor;
    public Object target;
    public Supplier<Object> getter;
    public Consumer<Object> setter;
    public Class<?> type;
    public String name;

    public static PropertyInfo create(PropertyDescriptor descriptor, Object target) {
        PropertyInfo info = new PropertyInfo();
        info.descriptor = descriptor;
        info.target = target;
        info.type = descriptor.getPropertyType();
        if (info.type == null) return null;
        info.getter = wrapGetter(descriptor, target);
        if (info.getter == null) return null;
        info.setter = wrapSetter(descriptor, target);
        info.name = descriptor.getName();
        return info;
    }

    public static Supplier<Object> wrapGetter(PropertyDescriptor prop, Object target) {
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

    public static Consumer<Object> wrapSetter(PropertyDescriptor prop, Object target) {
        Method write = prop.getWriteMethod();
        return (write == null) ? null : (value) -> {
            try {
                write.invoke(target, value);
            } catch (ReflectiveOperationException e) {
                String message = "Failed to call \"" + prop.getName() + "\" property setter";
                throw new RuntimeException(message, e);
            }
        };
    }

    public boolean isSettable() { return this.setter != null; }
    public boolean isBound() { return this.descriptor.isBound(); }
    public boolean isHidden() { return this.descriptor.isHidden(); }
    public boolean isExpert() { return this.descriptor.isExpert(); }
}

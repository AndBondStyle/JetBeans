package core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyInfo implements Cloneable {
    public PropertyDescriptor descriptor;
    public Object target;
    public Supplier<Object> getter;
    public Consumer<Object> setter;
    public Class<?> definer;
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
        info.definer = PropertyInfo.getDefiner(info);
        return info;
    }

    public static PropertyInfo rebind(PropertyInfo info, Object target) {
        try {
            PropertyInfo copy = (PropertyInfo) info.clone();
            copy.target = target;
            copy.getter = PropertyInfo.wrapGetter(info.descriptor, target);
            copy.setter = PropertyInfo.wrapSetter(info.descriptor, target);
            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static Class<?> getDefiner(PropertyInfo info) {
        Class<?> getterDefiner = info.descriptor.getReadMethod().getDeclaringClass();
        if (!info.isSettable()) return getterDefiner;
        Class<?> setterDefiner = info.descriptor.getWriteMethod().getDeclaringClass();
        return setterDefiner.isAssignableFrom(getterDefiner) ? getterDefiner : setterDefiner;
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

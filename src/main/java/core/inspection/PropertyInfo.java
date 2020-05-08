package core.inspection;

import java.awt.*;
import java.beans.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PropertyInfo implements Cloneable {
    public PropertyDescriptor descriptor;
    public Object target;
    public Supplier<Object> getter;
    public Consumer<Object> setter;
    public Class<?> definer;
    public Class<?> type;
    public String name;

    public static List<PropertyInfo> fetch(Object target) {
        try {
            // TODO: Refactor
            BeanInfo bi = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] descriptors = bi.getPropertyDescriptors();
            return Arrays.stream(bi.getPropertyDescriptors())
                    .map(x -> PropertyInfo.create(x, target))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IntrospectionException e) {
            String message = "Failed to analyze bean \"" + target + "\"";
            throw new RuntimeException(message, e);
        }
    }

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

    public static PropertyInfo bind(PropertyInfo info, Object target) {
        try {
            PropertyInfo copy = (PropertyInfo) info.clone();
            copy.target = target;
            copy.getter = PropertyInfo.wrapGetter(info.descriptor, target);
            copy.setter = PropertyInfo.wrapSetter(info.descriptor, target);
            return copy;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyInfo that = (PropertyInfo) o;
        if (!target.equals(that.target)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + name.hashCode();
        return result;
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

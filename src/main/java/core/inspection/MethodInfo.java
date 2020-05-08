package core.inspection;

import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class MethodInfo implements Cloneable {
    public MethodDescriptor descriptor;
    public Object target;
    public String name;
    public Class<?> definer;

    public static MethodInfo create(MethodDescriptor descriptor, Object target) {
        MethodInfo info = new MethodInfo();
        info.descriptor = descriptor;
        info.target = target;
        info.name = descriptor.getName();
        info.definer = descriptor.getMethod().getDeclaringClass();
        return info;
    }

    public static MethodInfo bind(MethodInfo info, Object target) {
        try {
            MethodInfo copy = (MethodInfo) info.clone();
            copy.target = target;
            return copy;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    public static boolean isRedundant(MethodInfo method, List<PropertyInfo> props, List<EventSetInfo> eventSets) {
        for (PropertyInfo prop : props) {
            Method read = prop.descriptor.getReadMethod();
            Method write = prop.descriptor.getWriteMethod();
            if (read != null && read.equals(method.descriptor.getMethod())) return true;
            if (write != null && write.equals(method.descriptor.getMethod())) return true;
        }
        for (EventSetInfo eventSet : eventSets) {
            Method add = eventSet.descriptor.getAddListenerMethod();
            Method remove = eventSet.descriptor.getRemoveListenerMethod();
            Method get = eventSet.descriptor.getGetListenerMethod();
            if (add != null && add.equals(method.descriptor.getMethod())) return true;
            if (remove != null && remove.equals(method.descriptor.getMethod())) return true;
            if (get != null && get.equals(method.descriptor.getMethod())) return true;
        }
        return false;
    }

    public String getName() {
        return this.descriptor.getDisplayName();
    }

    public int getParameterCount() {
        return this.descriptor.getMethod().getParameterCount();
    }

    public String getSignature() {
        StringBuilder result = new StringBuilder();
        Parameter[] parameters = this.descriptor.getMethod().getParameters();
        for (Parameter parameter : parameters) result.append(parameter.getType().getSimpleName()).append(", ");
        if (result.length() > 2) result.setLength(result.length() - 2);
        if (result.length() != 0) result.append(" ");
        result.append("-> ");
        result.append(this.descriptor.getMethod().getReturnType().getSimpleName());
        return result.toString();
    }
}

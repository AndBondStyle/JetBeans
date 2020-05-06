package core.inspection;

import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class MethodInfo implements Cloneable {
    public MethodDescriptor descriptor;
    public Object target;
    public Class<?> definer;

    public static MethodInfo create(MethodDescriptor descriptor, Object target) {
        MethodInfo info = new MethodInfo();
        info.descriptor = descriptor;
        info.target = target;
        info.definer = descriptor.getMethod().getDeclaringClass();
        return info;
    }

    public static MethodInfo bind(MethodInfo info, Object target) {
        try {
            MethodInfo copy = (MethodInfo) info.clone();
            copy.target = target;
            return copy;
        } catch (CloneNotSupportedException ignored) {
            System.err.println("BIND FAILED");
            return null;
        }
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

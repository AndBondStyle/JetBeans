package core.inspection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.*;
import java.util.stream.Collectors;

public class InstanceInfo implements Cloneable {
    static HashMap<Class<?>, InstanceInfo> cache = new HashMap<>();

    public Object target;
    public List<PropertyInfo> props;
    public List<EventSetInfo> events;
    public List<MethodInfo> methods;

    public static InstanceInfo fetch(Object target) {
        Class<?> klass = target.getClass();
        InstanceInfo info = InstanceInfo.cache.get(klass);
        if (info != null) return InstanceInfo.bind(info, target);

        BeanInfo bi = null;
        try {
            bi = Introspector.getBeanInfo(target.getClass());
        } catch (IntrospectionException e) {
            String message = "Failed to analyze instance \"" + target + "\"";
            throw new RuntimeException(message, e);
        }

        List<PropertyInfo> props = Arrays.stream(bi.getPropertyDescriptors())
                .map(x -> PropertyInfo.create(x, target))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<EventSetInfo> events = Arrays.stream(bi.getEventSetDescriptors())
                .map(x -> EventSetInfo.create(x, target))
                .collect(Collectors.toList());
        List<MethodInfo> methods = Arrays.stream(bi.getMethodDescriptors())
                .map(x -> MethodInfo.create(x, target))
                .filter(x -> !MethodInfo.isRedundant(x, props, events))
                .collect(Collectors.toList());

        info = new InstanceInfo();
        info.target = target;
        info.props = props;
        info.events = events;
        info.methods = methods;
        InstanceInfo.cache.put(klass, InstanceInfo.bind(info, null));
        return info;
    }

    public static InstanceInfo bind(InstanceInfo info, Object target) {
        try {
            InstanceInfo copy = (InstanceInfo) info.clone();
            copy.target = target;
            copy.props = info.props.stream().map(x -> PropertyInfo.bind(x, target)).collect(Collectors.toList());
            copy.events = info.events.stream().map(x -> EventSetInfo.bind(x, target)).collect(Collectors.toList());
            copy.methods = info.methods.stream().map(x -> MethodInfo.bind(x, target)).collect(Collectors.toList());
            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

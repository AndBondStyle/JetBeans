package core.inspection;

public class EventInfo {
    public Class<?> listenerClass;
    public EventSetInfo group;
    public MethodInfo method;
    public String name;
    public Object target;

    public static EventInfo create(EventSetInfo group, MethodInfo method) {
        EventInfo info = new EventInfo();
        info.listenerClass = group.descriptor.getListenerType();
        info.group = group;
        info.method = method;
        info.name = method.name;
        info.target = group.target;
        return info;
    }
}

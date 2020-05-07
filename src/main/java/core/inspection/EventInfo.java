package core.inspection;

public class EventInfo implements Cloneable {
    public EventSetInfo group;
    public MethodInfo method;
    public Object target;

    public static EventInfo create(EventSetInfo group, MethodInfo method) {
        EventInfo info = new EventInfo();
        info.group = group;
        info.method = method;
        info.target = group.target;
        return info;
    }

    public static EventInfo bind(EventInfo info, Object target) {
        try {
            EventInfo copy = (EventInfo) info.clone();
            copy.target = target;
            copy.method = MethodInfo.bind(info.method, target);
            return copy;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }
}

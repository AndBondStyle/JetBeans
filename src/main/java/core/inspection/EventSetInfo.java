package core.inspection;

import jdk.jfr.Event;

import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventSetInfo implements Cloneable {
    public EventSetDescriptor descriptor;
    public List<EventInfo> events;
    public Object target;

    public static EventSetInfo create(EventSetDescriptor descriptor, Object target) {
        EventSetInfo info = new EventSetInfo();
        info.descriptor = descriptor;
        info.target = target;
        info.events = info.initEvents();
        return info;
    }

    public static EventSetInfo bind(EventSetInfo info, Object target) {
        try {
            EventSetInfo copy = (EventSetInfo) info.clone();
            copy.target = target;
            copy.events = copy.initEvents();
            return copy;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    private List<EventInfo> initEvents() {
        return Arrays.stream(this.descriptor.getListenerMethodDescriptors())
                .map(x -> EventInfo.create(this, MethodInfo.create(x, this.target)))
                .collect(Collectors.toList());
    }

    public int getListenersCount() {
        try {
            Method m = this.descriptor.getGetListenerMethod();
            return ((Object[]) m.invoke(this.target)).length;
        } catch (ReflectiveOperationException ignored) {
            return 0;
        }
    }
}

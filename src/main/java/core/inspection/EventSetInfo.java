package core.inspection;


import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EventSetInfo implements Cloneable {
    public EventSetDescriptor descriptor;
    public List<EventInfo> events;
    public Consumer<Object> addListener;
    public Consumer<Object> removeListener;
    public Object target;

    public static EventSetInfo create(EventSetDescriptor descriptor, Object target) {
        EventSetInfo info = new EventSetInfo();
        info.descriptor = descriptor;
        info.target = target;
        info.events = info.initEvents();
        info.wrapAddListener();
        info.wrapRemoveListener();
        return info;
    }

    public static EventSetInfo bind(EventSetInfo info, Object target) {
        try {
            EventSetInfo copy = (EventSetInfo) info.clone();
            copy.target = target;
            copy.events = copy.initEvents();
            copy.wrapAddListener();
            copy.wrapRemoveListener();
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

    private void wrapAddListener() {
        Method method = this.descriptor.getAddListenerMethod();
        final Object target = this.target;
        final String name = this.descriptor.getName();
        this.addListener = (x) -> {
            try {
                method.invoke(target, x);
            } catch (ReflectiveOperationException e) {
                String message = "Failed to add " + name + " on " + target;
                throw new RuntimeException(message, e);
            }
        };
    }

    private void wrapRemoveListener() {
        Method method = this.descriptor.getRemoveListenerMethod();
        final Object target = this.target;
        final String name = this.descriptor.getName();
        this.removeListener = (x) -> {
            try {
                method.invoke(target, x);
            } catch (ReflectiveOperationException e) {
                String message = "Failed to remove " + name + " on " + target;
                throw new RuntimeException(message, e);
            }
        };
    }

    public int getListenersCount() {
        try {
            Method m = this.descriptor.getGetListenerMethod();
            if (m == null) return 0;
            return ((Object[]) m.invoke(this.target)).length;
        } catch (ReflectiveOperationException ignored) {
            return 0;
        }
    }
}

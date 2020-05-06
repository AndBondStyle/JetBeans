package core.inspection;

import java.beans.EventSetDescriptor;
import java.lang.reflect.Method;

public class EventSetInfo implements Cloneable {
    public EventSetDescriptor descriptor;
    public Object target;

    public static EventSetInfo create(EventSetDescriptor descriptor, Object target) {
        EventSetInfo info = new EventSetInfo();
        info.descriptor = descriptor;
        info.target = target;
        return info;
    }

    public static EventSetInfo bind(EventSetInfo info, Object target) {
        try {
            EventSetInfo copy = (EventSetInfo) info.clone();
            copy.target = target;
            return copy;
        } catch (CloneNotSupportedException ignored) {
            System.err.println("BIND FAILED");
            return null;
        }
    }

    public int getListenersCount() {
        try {
            Method m = this.descriptor.getGetListenerMethod();
            return ((Object[]) m.invoke(target)).length;
        } catch (ReflectiveOperationException ignored) {
            return 0;
        }
    }
}

package core.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface SimpleEventSupport {
    HashMap<Class, List<ActionListener>> registry = new HashMap<>();

    default void addListener(ActionListener listener) {
        this.registry.computeIfAbsent(this.getClass(), __ -> new ArrayList<>()).add(listener);
    }

    default void removeListener(ActionListener listener) {
        this.registry.computeIfAbsent(this.getClass(), __ -> new ArrayList<>()).remove(listener);
    }

    default void fireEvent(String data) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, data);
        List<ActionListener> listeners = this.registry.computeIfAbsent(this.getClass(), __ -> new ArrayList<>());
        for (ActionListener listener : listeners) listener.actionPerformed(e);
    }
}

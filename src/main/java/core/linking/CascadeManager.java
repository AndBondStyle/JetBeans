package core.linking;

import core.main.JetBeans;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

public class CascadeManager {
    public static int CALL_LIMIT = 10;
    public HashMap<Integer, Integer> visits = new HashMap<>();
    public boolean pendingCheck = false;
    public JetBeans core;

    public CascadeManager(JetBeans core) {
        this.core = core;
    }

    public void error() {
        this.core.logException(null, "Event cascade blocked after " + CALL_LIMIT + " iterations");
        this.visits.clear();
        this.pendingCheck = false;
    }

    public int getHash(String event, Object target) {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    public boolean begin(PropertyChangeEvent event, Object target) {
        int hash = this.getHash(event.getPropertyName(), target);
        int visits = this.visits.getOrDefault(hash, 0);
        if (visits == CALL_LIMIT) {
            if (!this.pendingCheck) {
                SwingUtilities.invokeLater(this::error);
                this.pendingCheck = true;
            }
            return false;
        }
        this.visits.put(hash, visits + 1);
        return true;
    }

    public void end(PropertyChangeEvent event, Object target) {
        int hash = this.getHash(event.getPropertyName(), target);
        Integer visits = this.visits.get(hash);
        if (visits == null || visits == 0) visits = 1;
        visits = visits == CALL_LIMIT ? 0 : (visits - 1);
        if (visits != 0) this.visits.put(hash, visits);
        else this.visits.remove(hash);
    }
}

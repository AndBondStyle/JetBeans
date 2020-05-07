package core;

import core.inspection.EventInfo;
import core.inspection.MethodInfo;
import core.inspection.PropertyInfo;
import gui.common.SimpleEventSupport;

public class Linker implements SimpleEventSupport {
    public boolean active = false;
    public Object source = null;
    public Object destination = null;

    public boolean isAcceptable(Object item) {
        if (!this.active) {
            if (item instanceof PropertyInfo) return ((PropertyInfo) item).isBound();
            return item instanceof EventInfo;
        } else {
            if (item instanceof PropertyInfo) {
                PropertyInfo prop = (PropertyInfo) item;
                return prop.isSettable() && !prop.equals(this.source);
            }
            return item instanceof MethodInfo;
        }
    }

    public void accept(Object item) {
        if (!active) {
            this.active = true;
            this.source = item;
            this.fireEvent("activate");
        } else {
            this.active = false;
            this.destination = item;
            this.link();
            this.fireEvent("deactivate");
        }
    }

    private void link() {
        if (this.destination != null) {

        }
        this.source = null;
        this.destination = null;
    }
}

package core.linking;

import core.JetBeans;
import core.inspection.PropertyInfo;

import com.intellij.openapi.project.Project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Function;
import java.awt.*;

public class PropertyLink extends LinkBase {
    public PropertyChangeListener listener;
    public PropertyInfo src;

    public PropertyLink(Project project, PropertyInfo src, Object dst) {
        this.project = project;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "Property \"" + this.src.name + "\"";
    }

    public void prepareDialog() {
        super.prepareDialog();
        if (this.dst instanceof PropertyInfo && this.src.type.equals(((PropertyInfo) this.dst).type)) {
            String body = this.evaluator.getBody();
            body = body.substring(0, body.length() - 3);
            this.evaluator.setBody(body + "curr);\n");
            if (this.listener == null) this.dialog.autoclose = true;
        }
        this.evaluator.setReturnType(null);
        this.evaluator.setParameters(
                new String[] {"src", "dst", "prev", "curr", "event"},
                new String[] {"source object", "destination object", "old property value", "new property value", "event object"},
                new Class[] {this.src.target.getClass(), this.destinationObject.getClass(), this.src.type, this.src.type, PropertyChangeEvent.class},
                null
        );
    }

    public void update() {
        this.script = this.evaluator.getScript();
        this.lambda = this.evaluator.makeLabmda();
        Object target = this.src.target;
        if (!(target instanceof Component)) return;
        Component comp = (Component) target;

        final Object source = this.src.target;
        final Function<Object[], Object> lambda = this.lambda;
        final Object destination = this.destinationObject;
        final CascadeManager cascade = JetBeans.getInstance(this.project).cascade;
        PropertyChangeListener listener = (e) -> {
            if (!cascade.begin(e, destination)) return;
            Object[] args = new Object[] {source, destination, e.getOldValue(), e.getNewValue(), e};
            try { lambda.apply(args); }
            catch (Throwable ignored) {}
            finally { cascade.end(e, destination); }
        };

        if (this.listener != null) comp.removePropertyChangeListener(this.src.name, this.listener);
        else this.fireEvent("created");
        comp.addPropertyChangeListener(this.src.name, listener);
        this.listener = listener;
        Object currentValue = this.src.getter.get();
        PropertyChangeEvent event = new PropertyChangeEvent(this.src.target, this.src.name, currentValue, currentValue);
        this.listener.propertyChange(event);
    }

    public void destroy() {
        if (this.listener != null) {
            Component target = (Component) this.src.target;
            target.removePropertyChangeListener(this.src.name, this.listener);
        }
    }
}

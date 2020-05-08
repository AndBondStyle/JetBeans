package core.links;

import gui.common.ShellInputDialog;
import gui.common.SimpleEventSupport;
import core.inspection.MethodInfo;
import core.inspection.PropertyInfo;
import core.Evaluator;

import com.intellij.openapi.project.Project;
import org.codehaus.commons.compiler.CompileException;

import java.beans.PropertyChangeListener;
import java.util.function.Consumer;
import java.util.function.Function;
import java.awt.*;

public class PropertyLink implements SimpleEventSupport {
    public Project project;
    public PropertyInfo src;
    public Object dst;

    public ShellInputDialog dialog;
    public Evaluator evaluator;
    public PropertyChangeListener listener;
    public Function<Object[], Object> lambda;
    public boolean isCurrentSession = true;
    public String script;

    public PropertyLink(Project project, PropertyInfo src, Object dst) {
        this.project = project;
        this.src = src;
        this.dst = dst;
    }

    public void init(String script) {
        this.prepareDialog();
        if (script != null) {
            this.script = script;
            this.isCurrentSession = false;
            this.evaluator.setBody(this.script);
            try {
                this.evaluator.cook();
                this.update();
            } catch (CompileException e) {
                throw new RuntimeException("Failed to restore link", e);
            }
        } else {
            if (this.script != null) this.evaluator.setScript(this.script);
            this.dialog.callback = this::update;
            this.dialog.init();
            this.dialog.show();
        }
    }

    private void prepareDialog() {
        this.dialog = new ShellInputDialog(this.project, "");
        this.evaluator = this.dialog.evaluator;
        this.dialog.execute = false;
        String title = "Shell Input - Link Property \"" + this.src.name + "\" -> ";
        Object destination = null;

        if (this.dst instanceof PropertyInfo) {
            PropertyInfo info = (PropertyInfo) this.dst;
            destination = info.target;
            title += "Property \"" + info.name + "\"";
            this.evaluator.setBody("\nreturn" + (this.src.type.equals(info.type) ? " curr" : "") + ";\n");
            this.evaluator.setReturnType(info.type);
        } else {
            MethodInfo info = (MethodInfo) this.dst;
            destination = info.target;
            title += "Method \"" + info.name + "\"";
            this.evaluator.setBody("\n// Method signature: " + info.getSignature() + "\ndst." + info.name + "();\n");
            this.evaluator.setReturnType(null);
        }

        this.dialog.setTitle(title);
        this.evaluator.setParameters(
                new String[] {"src", "dst", "prev", "curr"},
                new String[] {"source object", "destination object", "old property value", "new property value"},
                new Class[] {this.src.target.getClass(), destination.getClass(), this.src.type, this.src.type},
                null
        );
    }

    private void update() {
        this.script = this.evaluator.getScript();
        this.lambda = this.evaluator.makeLabmda();
        Object target = this.src.target;
        if (!(target instanceof Component)) return;
        Component comp = (Component) target;

        final Object source = this.src.target;
        final Function<Object[], Object> lambda = this.lambda;
        PropertyChangeListener listener;
        if (this.dst instanceof PropertyInfo) {
            PropertyInfo info = (PropertyInfo) this.dst;
            final Object destination = info.target;
            final Consumer<Object> setter = info.setter;
            listener = (e) -> {
                Object[] args = new Object[] {source, destination, e.getOldValue(), e.getNewValue()};
                setter.accept(lambda.apply(args));
            };
        } else {
            MethodInfo info = (MethodInfo) this.dst;
            final Object destination = info.target;
            listener = (e) -> {
                Object[] args = new Object[] {source, destination, e.getOldValue(), e.getNewValue()};
                lambda.apply(args);
            };
        }

        if (this.listener != null) comp.removePropertyChangeListener(this.src.name, this.listener);
        else this.fireEvent("created");
        comp.addPropertyChangeListener(this.src.name, listener);
        this.listener = listener;
    }
}

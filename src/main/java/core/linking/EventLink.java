package core.linking;

import core.main.JetBeans;
import core.inspection.EventInfo;

import com.intellij.openapi.project.Project;

import java.beans.ParameterDescriptor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EventLink extends LinkBase {
    public Object listener;
    public EventInfo src;

    public EventLink(Project project, EventInfo src, Object dst) {
        this.project = project;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "Event \"" + this.src.group.descriptor.getName() + "." + this.src.name + "\"";
    }

    public void prepareDialog() {
        super.prepareDialog();
        this.evaluator.setReturnType(null);
        List<String> arguments = new ArrayList<>(Arrays.asList("src", "dst"));
        List<String> descriptions = new ArrayList<>(Arrays.asList("source object", "destination object"));
        List<Class<?>> types = new ArrayList<>(Arrays.asList(this.src.target.getClass(), this.destinationObject.getClass()));
        ParameterDescriptor[] descriptors = this.src.method.descriptor.getParameterDescriptors();
        Parameter[] params = this.src.method.method.getParameters();
        if (descriptors != null) {
            for (int i = 0; i < params.length; i++) {
                arguments.add(descriptors[i].getName());
                descriptions.add(descriptors[i].getShortDescription());
                types.add(params[i].getType());
            }
        } else {
            for (Parameter param : params) {
                arguments.add(param.getName());
                descriptions.add("unknown");
                types.add(param.getType());
            }
        }
        this.evaluator.setParameters(
                arguments.toArray(String[]::new),
                descriptions.toArray(String[]::new),
                types.toArray(Class[]::new),
                null
        );
    }

    public void update() {
        this.script = this.evaluator.getScript();
        this.lambda = this.evaluator.makeLabmda();

        final Object source = this.src.target;
        final Function<Object[], Object> lambda = this.lambda;
        final Object destination = this.destinationObject;
        Object listener = Proxy.newProxyInstance(
                JetBeans.getInstance(this.project).loader,
                new Class[] {this.src.listenerClass},
                (proxy, method, args) -> {
                    if (!method.getName().equals(this.src.name)) return null;
                    List<Object> allArgs = new ArrayList<>();
                    allArgs.add(source);
                    allArgs.add(destination);
                    Collections.addAll(allArgs, args);
                    lambda.apply(allArgs.toArray());
                    return null;
                }
        );

        if (this.listener != null) {
            this.src.group.removeListener.accept(listener);
        } else {
            if (this.callback != null) this.callback.run();
            this.callback = null;
        }
        this.src.group.addListener.accept(listener);
        this.listener = listener;
    }
}

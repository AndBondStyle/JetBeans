package core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.codehaus.janino.ScriptEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Evaluator extends ScriptEvaluator {
    public String script = "";
    public Object[] arguments;
    public JetBeans core;

    public Evaluator(Project project) {
        // TODO: Set master loader
        this.core = JetBeans.getInstance(project);
    }

    public void setParameters(String[] names, String[] descriptions, Class<?>[] types, Object[] values) {
        this.setParameters(names, types);
        this.arguments = values;
        StringBuilder placeholder = new StringBuilder();
        placeholder.append("\n// --- Injected variables (name, type, description) ---\n");
        for (int i = 0; i < names.length; i++) {
            placeholder.append("// ").append(names[i]).append(" - ");
            placeholder.append(types[i].getCanonicalName());
            if (descriptions[i] != null) placeholder.append(" (").append(descriptions[i]).append(")");
            placeholder.append("\n");
        }
        this.script = placeholder.toString() + this.script;
    }

    public Function<Object[], Object> makeLabmda() {
        return (Object[] arguments) -> {
            try { return this.evaluate(arguments); }
            catch (InvocationTargetException e) { this.core.logException(e); }
            return null;
        };
    }

    public static Pair<String, String> splitHeader(String text) {
        String[] lines = text.lines().toArray(String[]::new);
        StringBuilder header = new StringBuilder();
        StringBuilder remainder = new StringBuilder();
        if (lines.length == 0) return new Pair<>("", "");
        int i = 0;
        while (i < lines.length && lines[i].isBlank()) i++;
        while (i < lines.length && !lines[i].isBlank()) header.append(lines[i++]).append("\n");
        while (i < lines.length) remainder.append(lines[i++]).append("\n");
        if (remainder.length() > 1) remainder.setLength(remainder.length() - 1);
        return new Pair<>(header.toString(), remainder.toString());
    }
}

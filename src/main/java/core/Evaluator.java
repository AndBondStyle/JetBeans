package core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Evaluator extends ScriptEvaluator {
    private static String DEFAULT_HEADER_TEXT = "" +
            "// Execute arbitrary java code optionally ending with return statement\n" +
            "// First consecutive non-empty lines are persistent:\n" +
            "// use them to store import statements you're frequently using\n" +
            "import javax.swing.*;\nimport java.awt.*;\n" +
            "// --- End of persistent section ---\n";
    private static String staticHeader = DEFAULT_HEADER_TEXT;

    private String header = "";
    private String body = "\nreturn ;\n";
    private Object[] arguments = new Object[0];
    private JetBeans core;

    public Evaluator(Project project, String body, boolean useHeader) {
        // TODO: Master class loader
        this.core = JetBeans.getInstance(project);
        if (body != null) this.body = body;
        if (useHeader) this.header = Evaluator.staticHeader;
    }

    public String getScript() { return this.header + this.body; }
    public void setScript(String script) { this.header = ""; this.body = script; }
    public String getBody() { return this.body; }
    public void setBody(String body) { this.body = body; }
    public void cook() throws CompileException { this.cook(this.getScript()); }
    public Object evaluate() throws InvocationTargetException { return this.evaluate(this.arguments); }

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
        this.body = placeholder.toString() + this.body;
    }

    public Function<Object[], Object> makeLabmda() {
        return (Object[] arguments) -> {
            try { return this.evaluate(arguments); }
            catch (InvocationTargetException e) { this.core.logException(e); }
            return null;
        };
    }

    public void updateHeader() {
        Pair<String, String> parts = Evaluator.splitHeader(this.getScript());
        if (parts.getFirst().equals("")) return;
        Evaluator.staticHeader = parts.getFirst();
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

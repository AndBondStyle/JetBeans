package core.linking;

import com.intellij.openapi.project.Project;
import core.inspection.MethodInfo;
import core.inspection.PropertyInfo;
import gui.common.ShellInputDialog;
import gui.common.SimpleEventSupport;
import core.main.Evaluator;

import org.codehaus.commons.compiler.CompileException;
import java.util.function.Function;

public abstract class LinkBase implements SimpleEventSupport {
    public Project project;
    public Object dst;

    public ShellInputDialog dialog;
    public Evaluator evaluator;
    public Object destinationObject;
    public Function<Object[], Object> lambda;
    public boolean isCurrentSession = true;
    public String script;

    public void prepareDialog() {
        this.dialog = new ShellInputDialog(this.project, "");
        this.evaluator = this.dialog.evaluator;
        this.dialog.execute = false;
        this.dialog.setTitle("Shell Input - Link " + this.toString());

        if (this.dst instanceof PropertyInfo) {
            PropertyInfo info = (PropertyInfo) this.dst;
            this.destinationObject = info.target;
            String method = info.descriptor.getWriteMethod().getName();
            this.evaluator.setBody("\n// Target property type: " + info.type.getName() + "\ndst." + method + "();\n");
        } else {
            MethodInfo info = (MethodInfo) this.dst;
            this.destinationObject = info.target;
            this.evaluator.setBody("\n// Method signature: " + info.getSignature() + "\ndst." + info.name + "();\n");
        }
    }

    public void update() {}

    public void destroy() {}

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

    @Override
    protected void finalize() throws Throwable {
        System.out.println("LinkBase finalized");
        super.finalize();
    }
}

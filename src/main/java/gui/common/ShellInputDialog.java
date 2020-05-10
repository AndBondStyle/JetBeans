package gui.common;

import core.main.Evaluator;

import java.lang.reflect.InvocationTargetException;
import com.intellij.ide.highlighter.JShellFileType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.codehaus.commons.compiler.InternalCompilerException;
import org.codehaus.commons.compiler.CompileException;

import javax.swing.*;
import java.awt.*;

public class ShellInputDialog extends DialogWrapper {
    public Project project;
    public int offset = 2;
    public Runnable callback = null;
    public boolean execute = true;
    public boolean autoclose = false;

    public com.intellij.openapi.editor.Editor code;
    public Evaluator evaluator;
    public Object result;

    public ShellInputDialog(Project project, String title) {
        super(project, true, IdeModalityType.MODELESS);
        this.setOKButtonText("Evaluate");
        this.setTitle(title);
        this.project = project;
        this.evaluator = new Evaluator(project, null, true);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void show() {
        if (this.autoclose) this.doOKAction();
        else super.show();
    }

    @Override
    protected String getDimensionServiceKey() {
        return "JetBeans:ShellInputDialog";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.code.getContentComponent();
    }

    @Override
    protected void dispose() {
        EditorFactory.getInstance().releaseEditor(this.code);
        super.dispose();
    }

    @Override
    protected JComponent createCenterPanel() {
        Document document = EditorFactory.getInstance().createDocument(this.evaluator.getScript());
        this.code = EditorFactory.getInstance().createEditor(document, this.project, JShellFileType.INSTANCE, false);
        this.code.getCaretModel().moveToOffset(this.code.getDocument().getTextLength() - this.offset);
        this.code.getSettings().setLineMarkerAreaShown(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this.code.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void doOKAction() {
        try {
            this.evaluator.setScript(this.code.getDocument().getText());
            this.evaluator.cook();
            if (this.execute) {
                // Just get compute result and return
                this.result = this.evaluator.evaluate();
                this.evaluator.updateHeader();
            }
            if (this.callback != null) {
                try {
                    this.callback.run();
                    super.doOKAction();
                } catch (RuntimeException e) {
                    this.setErrorText("Runtime error: " + e.getMessage());
                }
            }
        } catch (CompileException e) {
            this.setErrorText("Compile error: " + e.getMessage());
            if (this.autoclose) super.show();
        } catch (InvocationTargetException e) {
            this.setErrorText("Evaluation error: " + e.getMessage());
            if (this.autoclose) super.show();
        } catch (InternalCompilerException e) {
            this.setErrorText("Fatal compile error: " + e.getMessage());
            if (this.autoclose) super.show();
        }
    }
}

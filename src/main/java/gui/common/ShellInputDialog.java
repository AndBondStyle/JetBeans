package gui.common;

import core.Evaluator;

import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiFile;

import org.codehaus.commons.compiler.CompileException;
import java.lang.reflect.InvocationTargetException;
import com.intellij.ide.highlighter.JShellFileType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class ShellInputDialog extends DialogWrapper {
    public Project project;
    public int offset = 2;
    public Runnable callback = null;
    public boolean execute = true;

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
        PsiFile psi = PsiFileFactory.getInstance(this.project)
                .createFileFromText("_", JShellFileType.INSTANCE, this.evaluator.getScript(), 0, true);
        Document document = psi.getViewProvider().getDocument();
        // TODO: Fix document / PSI mismatch error
        // Document document = PsiDocumentManager.getInstance(this.project).getDocument(psi);
        // Document document = EditorFactory.getInstance().createDocument(text);
        assert document != null;
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
            if (this.callback != null) this.callback.run();
            super.doOKAction();
        } catch (CompileException e) {
            this.setErrorText("Compile error: " + e.getMessage());
        } catch (InvocationTargetException e) {
            this.setErrorText("Evaluation error: " + e.getMessage());
        }
    }
}

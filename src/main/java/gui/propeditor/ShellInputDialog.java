package gui.propeditor;

import gui.propeditor.editors.Editor;

import org.codehaus.commons.compiler.CompileException;
import java.lang.reflect.InvocationTargetException;
import com.intellij.lang.java.JShellLanguage;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

import org.codehaus.janino.*;
import com.intellij.psi.*;
import javax.swing.*;
import java.awt.*;

public class ShellInputDialog extends DialogWrapper {
    static String DEFAULT_INITIAL_TEXT = "" +
            "// Execute arbitrary java code ending with return statement.\n" +
            "// First consecutive non-empty lines are persistent:\n" +
            "// use them to store import statements you're frequently using\n" +
            "import javax.swing.*;\nimport java.awt.*;\n" +
            "// --- End of persistent section ---\n";
    static String initialText = DEFAULT_INITIAL_TEXT;

    private com.intellij.openapi.editor.Editor code;
    private Project project;
    private Editor editor;

    public ShellInputDialog(Project project, Editor editor) {
        super(project, true, IdeModalityType.PROJECT);
        this.setTitle("Shell Input");
        this.setOKButtonText("Evaluate");
        this.project = project;
        this.editor = editor;
        this.init();
    }

    @Override
    protected String getDimensionServiceKey() {
        return "JetBeans:ShellInputDialog";
    }

    @Override
    protected JComponent createCenterPanel() {
        String text = ShellInputDialog.initialText + "\nreturn ;\n";
        PsiFile file = PsiFileFactory.getInstance(this.project).createFileFromText(JShellLanguage.INSTANCE, text);
        Document document = PsiDocumentManager.getInstance(this.project).getDocument(file);
        this.code = EditorFactory.getInstance().createEditor(document, this.project, file.getVirtualFile().getFileType(), false);
        this.code.getCaretModel().moveToOffset(this.code.getDocument().getTextLength() - 2);
        this.code.getSettings().setLineMarkerAreaShown(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this.code.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.code.getContentComponent();
    }

    @Override
    protected void doOKAction() {
        try {
            String text = this.code.getDocument().getText();
            ScriptEvaluator se = new ScriptEvaluator();
            se.setReturnType(this.editor.prop.type);
            se.cook(text);
            Object value = se.evaluate(new Object[0]);
            this.editor.accept(value, true);
            this.updateInitialText();
            super.doOKAction();
        } catch (CompileException e) {
            this.setErrorText("Compile error: " + e.getMessage());
        } catch (InvocationTargetException e) {
            this.setErrorText("Evaluation error: " + e.getMessage());
        }
    }

    private void updateInitialText() {
        String text = this.code.getDocument().getText();
        String[] lines = text.lines().toArray(String[]::new);
        StringBuilder builder = new StringBuilder();
        if (lines.length == 0) return;
        int i = 0;
        while (i < lines.length && lines[i].isBlank()) i++;
        while (i < lines.length && !lines[i].isBlank()) builder.append(lines[i++]).append("\n");
        ShellInputDialog.initialText = builder.toString();
    }
}

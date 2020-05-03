package gui.propeditor.shell;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vcs.changes.ui.CommitDialogChangesBrowser;
import com.intellij.openapi.vcs.configurable.CommitDialogConfigurable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.EditorTextField;
import gui.propeditor.editors.Editor;
import org.apache.batik.dom.util.DocumentFactory;
import org.codehaus.commons.compiler.CompileException;
import org.jetbrains.annotations.NotNull;
import org.codehaus.janino.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShellInputDialog extends DialogWrapper {
    static String INITIAL_TEXT = "// JShell interactive input: " +
            "execute arbitary java code ending with return statement \n\n" +
            "return ";
    static String RETURN_PATTERN = "(?md)^\\s*return (.+);\\s*$";

    private com.intellij.openapi.editor.Editor jbEditor;
    private EditorTextField prompt;
    private Project project;
    private Editor<?> editor;

    public ShellInputDialog(Project project, Editor<?> editor) {
        super(project, false, IdeModalityType.PROJECT);
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
        JavaCodeFragment code = JavaCodeFragmentFactory.getInstance(this.project).createCodeBlockCodeFragment(INITIAL_TEXT, null, true);
        Document document = PsiDocumentManager.getInstance(this.project).getDocument(code);
        this.jbEditor = EditorFactory.getInstance().createEditor(document, this.project, JavaFileType.INSTANCE, false);
        this.jbEditor.getSettings().setLineNumbersShown(false);
        this.jbEditor.getSettings().setLineMarkerAreaShown(false);
        this.jbEditor.getMarkupModel().removeAllHighlighters();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this.jbEditor.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.prompt;
    }

    @Override
    protected void doOKAction() {
        String text = this.jbEditor.getDocument().getText();
        ScriptEvaluator se = new ScriptEvaluator();
        se.setReturnType(this.editor.type);
        try {
            se.cook(text);
        } catch (CompileException e) {
            System.err.println("COMPILE ERROR: ");
            e.printStackTrace();
            // TODO: Display error
            return;
        }
        try {
            Object value = se.evaluate(new Object[0]);
            this.editor.accept(value, true);
            super.doOKAction();
        } catch (InvocationTargetException e) {
            System.err.println("EVALUATE ERROR: ");
            e.printStackTrace();
            // TODO: Display error
        }
    }

    @Override
    protected ValidationInfo doValidate() {
        String text = this.jbEditor.getDocument().getText();
        Pattern pattern = Pattern.compile(RETURN_PATTERN);
        Matcher matcher = pattern.matcher(text);
        if (matcher.results().count() == 0) return new ValidationInfo("Missing return statement");
        if (matcher.results().count() > 1) return new ValidationInfo("Multiple return statements");
        return null;
    }
}

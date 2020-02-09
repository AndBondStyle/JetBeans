package integration;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class CustomNewFileAction extends CreateFileAction {
    CustomNewFileAction() {
        super("JetBeans Component", "Create new JetBeans component", CustomFileType.ICON);
    }

    class Validator extends MyValidator {
        private final String EXT = "." + CustomFileType.INSTANCE.getDefaultExtension();

        public Validator(Project project, PsiDirectory directory) {
            super(project, directory);
        }

        private String forceExtension(String input) {
            input = input.trim();
            if (!input.isEmpty() && !input.endsWith(EXT)) input += EXT;
            return input;
        }

        @Override
        public boolean checkInput(String inputString) {
            return super.checkInput(forceExtension(inputString));
        }

        @Override
        public boolean canClose(String inputString) {
            return super.canClose(forceExtension(inputString));
        }
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(final Project project, PsiDirectory directory) {
        Validator validator = new Validator(project, directory);
        Messages.showInputDialog(
                project,
                IdeBundle.message("prompt.enter.new.file.name"),
                IdeBundle.message("title.new.file"),
                null, null, validator
        );
        return validator.getCreatedElements();
    }
}

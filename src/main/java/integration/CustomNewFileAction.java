package integration;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileSystemTree;
import com.intellij.openapi.fileChooser.actions.NewFileAction;

public class CustomNewFileAction extends NewFileAction {
    @Override
    protected void actionPerformed(FileSystemTree fileSystemTree, AnActionEvent e) {
        super.actionPerformed(fileSystemTree, e);
    }
}

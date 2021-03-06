package core.actions;

import gui.library.LibraryView;
import core.main.JetBeans;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class ImportAction extends AnAction implements DumbAware {
    public static FileType JAR = FileTypeRegistry.getInstance().getFileTypeByExtension("jar");
    public static FileType CLASS = FileTypeRegistry.getInstance().getFileTypeByExtension("class");
    public static FileType JAVA = FileTypeRegistry.getInstance().getFileTypeByExtension("java");

    public ImportAction() {
        super("Import Classes", "Import classes", AllIcons.Actions.Download);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JetBeans core = JetBeans.getInstance(e.getProject());
        FileChooserDescriptor d = ImportAction.getFileChooserDescriptor();
        FileChooser.chooseFile(d, e.getProject(), null, file -> {
            try {
                core.loader.loadFile(file.getPath(), true);
                LibraryView view = e.getProject().getService(LibraryView.class);
                view.setActiveTab(LibraryView.CLASSES_TAB);
            } catch (Exception err) {
                err = new RuntimeException("Failed to load " + file.getPath(), err);
                core.logException(err, "Import error");
            }
        });
    }

    public static FileChooserDescriptor getFileChooserDescriptor() {
        return new FileChooserDescriptor(true, false, true, true, false, false)
                .withFileFilter(file -> {
                    if (FileTypeRegistry.getInstance().isFileOfType(file, JAR)) return true;
                    if (FileTypeRegistry.getInstance().isFileOfType(file, CLASS)) return true;
                    return false;
                });
    }
}

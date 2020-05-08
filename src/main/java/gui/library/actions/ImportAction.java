package gui.library.actions;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import gui.library.LibraryView;
import core.registry.loaders.JarLoader;
import core.JetBeans;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import java.util.jar.JarFile;

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
        FileChooserDescriptor d = new FileChooserDescriptor(true, false, true, true, false, false)
                .withFileFilter(file -> {
                    if (FileTypeRegistry.getInstance().isFileOfType(file, JAR)) return true;
                    if (FileTypeRegistry.getInstance().isFileOfType(file, CLASS)) return true;
                    if (FileTypeRegistry.getInstance().isFileOfType(file, JAVA)) return true;
                    return false;
                });
        FileChooser.chooseFile(d, e.getProject(), null, file -> {
            try {
                core.loader.loadFile(file.getPath());
                LibraryView view = e.getProject().getService(LibraryView.class);
                view.setActiveTab(LibraryView.CLASSES_TAB);
            } catch (Exception err) {
                err = new RuntimeException("Failed to load " + file.getPath(), err);
                core.logException(err, "Import error");
            }
        });
    }
}

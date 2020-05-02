package gui.library.actions;

import core.registry.loaders.JarLoader;
import core.JetBeans;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class LoadJarAction extends AnAction implements DumbAware {
    public LoadJarAction() {
        super("Import Jar", "Import classes from jar", AllIcons.Actions.Download);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JetBeans core = JetBeans.getInstance(e.getProject());
        FileChooserDescriptor d = new FileChooserDescriptor(true, false, true, true, false, false);
        FileChooser.chooseFile(d, e.getProject(), null, file -> {
            JarLoader loader = new JarLoader(file.getPath());
            core.getRegistry().add(loader);
        });
    }
}

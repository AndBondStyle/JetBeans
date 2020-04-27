package gui.library.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
//import core.JetBeans;
//import core.registry.loaders.ClassLoaderEx;
//import core.registry.loaders.HardcodedLoader;
import ide.CustomFileType;
import org.jetbrains.annotations.NotNull;

public class TestAction extends AnAction implements DumbAware {
    public TestAction() {
        super("Test Action", "Performs test action", CustomFileType.ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        JetBeans core = e.getProject().getService(JetBeans.class);
//        ClassLoaderEx loader = new HardcodedLoader("Swing 2", "javax.swing.*");
//        core.getRegistry().register(loader, "javax.swing.JButton");
    }
}

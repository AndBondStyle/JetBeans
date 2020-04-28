package gui.library.actions;

import core.registry.loaders.SimpleLoader;
import ide.CustomFileType;
import core.JetBeans;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class TestAction extends AnAction implements DumbAware {
    public TestAction() {
        super("Test Action", "Performs test action", CustomFileType.ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JetBeans core = JetBeans.getInstance(e.getProject());
        SimpleLoader loader = new SimpleLoader("Swing", "javax.swing");
        loader.add("JComboBox");
        core.getRegistry().add(loader);
        loader = new SimpleLoader("AWT", "java.awt");
        loader.add("Button");
        core.getRegistry().add(loader);
    }
}
